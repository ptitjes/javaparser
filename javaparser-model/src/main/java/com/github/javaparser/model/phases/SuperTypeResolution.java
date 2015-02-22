package com.github.javaparser.model.phases;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.TypeParameterElem;
import com.github.javaparser.model.report.Reporter.Severity;
import com.github.javaparser.model.scope.EltNames;
import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.ScopeException;
import com.github.javaparser.model.source.SourceOrigin;
import com.github.javaparser.model.type.*;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.javaparser.model.source.utils.NodeListUtils.visitAll;

/**
 * @author Didier Villevalois
 */
public class SuperTypeResolution {

	private final Analysis analysis;
	private final TypeUtils typeUtils;

	public SuperTypeResolution(Analysis analysis) {
		this.analysis = analysis;

		typeUtils = analysis.getTypeUtils();
	}

	public void process() {
		for (PackageElement packageElement : analysis.getSourcePackageElements()) {
			scanner.scan(packageElement);
		}
	}

	private ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {

		@Override
		public Void visitType(TypeElement e, Void aVoid) {
			TypeElem typeElem = (TypeElem) e;
			SourceOrigin origin = (SourceOrigin) typeElem.origin();
			Node node = origin.getNode();

			node.accept(discriminator, typeElem);

			return super.visitType(e, aVoid);
		}
	};

	private VoidVisitorAdapter<TypeElem> discriminator = new VoidVisitorAdapter<TypeElem>() {

		@Override
		public void visit(ClassOrInterfaceDeclaration n, TypeElem arg) {
			// TODO First process type parameters' bounds' type refs

			List<ClassOrInterfaceType> extended = n.getExtends();
			List<ClassOrInterfaceType> implemented = n.getImplements();

			if (n.isInterface()) {
				arg.setSuperClass(NoTpe.NONE);

				try {
					arg.setInterfaces(resolveTypes(extended, arg));
				} catch (ScopeException ex) {
					analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), arg.origin());
				}
			} else {
				if (extended != null && extended.size() == 1) {
					try {
						ClassOrInterfaceType type = extended.get(0);
						arg.setSuperClass(resolveType(type, arg));
					} catch (ScopeException ex) {
						analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), arg.origin());
					}
				} else arg.setSuperClass(NoTpe.NONE);

				try {
					arg.setInterfaces(resolveTypes(implemented, arg));
				} catch (ScopeException ex) {
					analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), arg.origin());
				}
			}
		}

		@Override
		public void visit(EnumDeclaration n, TypeElem arg) {
			List<ClassOrInterfaceType> implemented = n.getImplements();

			arg.setSuperClass(typeUtils.enumTypeOf(arg.asType()));

			try {
				arg.setInterfaces(resolveTypes(implemented, arg));
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), arg.origin());
			}
		}

		@Override
		public void visit(AnnotationDeclaration n, TypeElem arg) {
			arg.setSuperClass(NoTpe.NONE);
			arg.setInterfaces(Collections.<TpeMirror>singletonList(typeUtils.annotationType()));
		}
	};

	private List<TpeMirror> resolveTypes(List<? extends Type> types, TypeElem fromElem) {
		List<TpeMirror> tpeMirrors = new ArrayList<TpeMirror>();
		if (types != null) {
			for (Type type : types) {
				tpeMirrors.add(resolveType(type, fromElem));
			}
		}
		return tpeMirrors;
	}

	private TpeMirror resolveType(Type type, TypeElem fromElem) {
		TpeMirror tpeMirror = type.accept(typeResolver, fromElem);
		if (tpeMirror == null) {
			throw new ScopeException("Can't find type '" + type + "'", null);
		}
		return tpeMirror;
	}

	private GenericVisitor<TpeMirror, TypeElem> typeResolver = new GenericVisitorAdapter<TpeMirror, TypeElem>() {
		@Override
		public TpeMirror visit(ClassOrInterfaceType n, TypeElem arg) {
			ClassOrInterfaceType typeScope = n.getScope();
			EltSimpleName typeName = EltNames.makeSimple(n.getName());
			List<Type> typeArgs = n.getTypeArgs();

			// TODO Add consistency checks
			// - Static/Instance type members
			// - No scope and no type args on type vars

			TypeParameterElem typeParameterElem = arg.scope().resolveTypeParameter(typeName);
			if (typeParameterElem != null) {
				// TODO Fix bounds
				return new TpeVariable(typeParameterElem, null, null);
			} else {
				List<TpeMirror> tpeArgsMirrors = visitAll(this, arg, typeArgs);

				if (typeScope != null) {
					DeclaredTpe typeScopeMirror = (DeclaredTpe) typeScope.accept(this, arg);
					TypeElem typeScopeElem = typeScopeMirror.asElement();
					TypeElem typeElem = typeScopeElem.scope().resolveType(typeName);
					return new DeclaredTpe(typeScopeMirror, typeElem, tpeArgsMirrors);
				} else {
					TypeElem typeElem = arg.scope().resolveType(typeName);
					return new DeclaredTpe(NoTpe.NONE, typeElem, tpeArgsMirrors);
				}
			}
		}

		@Override
		public TpeMirror visit(ReferenceType n, TypeElem arg) {
			int depth = n.getArrayCount();
			Type type = n.getType();
			TpeMirror tpeMirror = type.accept(this, arg);
			return makeArray(tpeMirror, depth);
		}

		private TpeMirror makeArray(TpeMirror tpeMirror, int depth) {
			if (depth == 0) return tpeMirror;
			else return makeArray(new ArrayTpe(tpeMirror), depth);
		}

		@Override
		public TpeMirror visit(WildcardType n, TypeElem arg) {
			// TODO Implement
			return null;
		}

		@Override
		public TpeMirror visit(PrimitiveType n, TypeElem arg) {
			switch (n.getType()) {
				case Boolean:
					return PrimitiveTpe.BOOLEAN;
				case Char:
					return PrimitiveTpe.CHAR;
				case Byte:
					return PrimitiveTpe.BYTE;
				case Short:
					return PrimitiveTpe.SHORT;
				case Int:
					return PrimitiveTpe.INT;
				case Long:
					return PrimitiveTpe.LONG;
				case Float:
					return PrimitiveTpe.FLOAT;
				case Double:
					return PrimitiveTpe.DOUBLE;
				default:
					throw new IllegalArgumentException();
			}
		}

		@Override
		public TpeMirror visit(VoidType n, TypeElem arg) {
			return NoTpe.VOID;
		}
	};
}
