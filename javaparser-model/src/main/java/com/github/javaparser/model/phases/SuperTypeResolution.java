package com.github.javaparser.model.phases;

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
import javax.lang.model.type.TypeKind;
import javax.lang.model.util.ElementScanner8;
import javax.lang.model.util.Types;
import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.model.source.utils.NodeListUtils.visitAll;
import static com.github.javaparser.model.source.utils.SrcNameUtils.asName;

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
		for (PackageElement packageElement : analysis.getPackageElements()) {
			scanner.scan(packageElement);
		}
	}

	private void process(TypeElem e, ClassOrInterfaceDeclaration n) {
		// TODO First process type parameters' bounds' type refs

		List<ClassOrInterfaceType> extended = n.getExtends();
		List<ClassOrInterfaceType> implemented = n.getImplements();

		if (n.isInterface()) {
			e.setSuperClass(NoTpe.NONE);

			try {
				List<TpeMirror> interfaces = new ArrayList<TpeMirror>();
				if (implemented != null) {
					for (ClassOrInterfaceType type : extended) {
						TpeMirror typeMirror = resolveType(type, e);
						interfaces.add(typeMirror);
					}
				}
				e.setInterfaces(interfaces);
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
			}
		} else {
			try {
				if (extended != null && extended.size() == 1) {
					ClassOrInterfaceType type = extended.get(0);
					TpeMirror typeMirror = resolveType(type, e);
					e.setSuperClass(typeMirror);
				} else e.setSuperClass(NoTpe.NONE);
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
			}

			try {
				List<TpeMirror> interfaces = new ArrayList<TpeMirror>();
				if (implemented != null) {
					for (ClassOrInterfaceType type : implemented) {
						TpeMirror typeMirror = resolveType(type, e);
						interfaces.add(typeMirror);
					}
				}
				e.setInterfaces(interfaces);
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
			}
		}
	}

	private void process(TypeElem e, EnumDeclaration n) {
		List<ClassOrInterfaceType> implemented = n.getImplements();

		try {
			List<TpeMirror> interfaces = new ArrayList<TpeMirror>();
			if (implemented != null) {
				for (ClassOrInterfaceType type : implemented) {
					TpeMirror typeMirror = resolveType(type, e);
					interfaces.add(typeMirror);
				}
			}
			e.setInterfaces(interfaces);
		} catch (ScopeException ex) {
			analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
		}
	}

	public TpeMirror resolveType(Type type, TypeElem fromElem) {
		return type.accept(typeResolver, fromElem);
	}

	private ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {

		@Override
		public Void visitType(TypeElement e, Void aVoid) {
			TypeElem typeElem = (TypeElem) e;
			SourceOrigin origin = (SourceOrigin) typeElem.origin();
			origin.getNode().accept(discriminator, typeElem);

			return super.visitType(e, aVoid);
		}
	};

	private VoidVisitorAdapter<TypeElem> discriminator = new VoidVisitorAdapter<TypeElem>() {

		@Override
		public void visit(ClassOrInterfaceDeclaration n, TypeElem arg) {
			process(arg, n);
		}

		@Override
		public void visit(EnumDeclaration n, TypeElem arg) {
			process(arg, n);
		}
	};

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
					TypeElem typeElem = arg.parentScope().resolveType(typeName);
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
