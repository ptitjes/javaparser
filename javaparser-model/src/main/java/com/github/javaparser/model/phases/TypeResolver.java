package com.github.javaparser.model.phases;

import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.TypeParameterElem;
import com.github.javaparser.model.scope.EltNames;
import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.scope.ScopeException;
import com.github.javaparser.model.source.SourceOrigin;
import com.github.javaparser.model.type.*;

import java.util.*;

import static com.github.javaparser.model.source.utils.NodeListUtils.visitAll;

/**
 * @author Didier Villevalois
 */
public class TypeResolver implements Registry.Participant {

	private Classpath classpath;
	private TypeUtils typeUtils;

	@Override
	public void configure(Registry registry) {
		classpath = registry.get(Classpath.class);
		typeUtils = registry.get(TypeUtils.class);
	}

	public List<TpeMirror> resolveTypes(List<? extends Type> types, Scope scope) {
		List<TpeMirror> tpeMirrors = new ArrayList<TpeMirror>();
		if (types != null) {
			for (Type type : types) {
				tpeMirrors.add(resolveType(type, scope));
			}
		}
		return tpeMirrors;
	}

	public TpeMirror resolveType(Type type, Scope scope) {
		TpeMirror tpeMirror = type.accept(typeResolver, scope);
		if (tpeMirror == null) {
			throw new ScopeException("Can't resolve type '" + type + "'");
		}
		return tpeMirror;
	}

	public void resolveTypeParameters(List<TypeParameterElem> typeParameters, Scope scope) {
		CycleDetectingTypeVisitor cdtResolver = new CycleDetectingTypeVisitor();
		for (TypeParameterElem typeParameterElem : typeParameters) {
			cdtResolver.resolveBounds(typeParameterElem, scope);
		}
	}

	private TypeElem findTypeElem(EltSimpleName typeName, Scope scope) {
		TypeElem typeElem = scope.resolveType(typeName);
		if (typeElem == null) {
			throw new ScopeException("Can't find type '" + typeName + "'");
		}
		return typeElem;
	}

	private TypeVisitor typeResolver = new TypeVisitor();

	class TypeVisitor extends GenericVisitorAdapter<TpeMirror, Scope> {

		protected void resolveBounds(TypeParameterElem typeParameterElem, Scope scope) {
			List<TpeMirror> boundsMirrors = typeParameterElem.getBounds();
			if (boundsMirrors == null)
				throw new ScopeException("Type parameter bounds not resolved for '" +
						typeParameterElem.getSimpleName() + "'");
		}

		@Override
		public TpeMirror visit(ClassOrInterfaceType n, Scope arg) {
			ClassOrInterfaceType typeScope = n.getScope();
			EltSimpleName typeName = EltNames.makeSimple(n.getName());
			List<Type> typeArgs = n.getTypeArgs();

			// TODO Add consistency checks
			// - Static/Instance type members
			// - No scope and no type args on type vars

			TypeParameterElem typeParameterElem = arg.resolveTypeParameter(typeName);
			if (typeParameterElem != null) {
				resolveBounds(typeParameterElem, arg);
				return typeParameterElem.asType();
			} else {
				List<TpeMirror> tpeArgsMirrors = visitAll(this, arg, typeArgs);

				if (typeScope != null) {
					DeclaredTpe typeScopeMirror = (DeclaredTpe) typeScope.accept(this, arg);
					TypeElem typeScopeElem = typeScopeMirror.asElement();
					TypeElem typeElem = findTypeElem(typeName, typeScopeElem.scope());
					return new DeclaredTpe(typeScopeMirror, typeElem, tpeArgsMirrors);
				} else {
					TypeElem typeElem = findTypeElem(typeName, arg);
					return new DeclaredTpe(NoTpe.NONE, typeElem, tpeArgsMirrors);
				}
			}
		}

		@Override
		public TpeMirror visit(ReferenceType n, Scope arg) {
			int depth = n.getArrayCount();
			Type type = n.getType();
			TpeMirror tpeMirror = type.accept(this, arg);
			return makeArray(tpeMirror, depth);
		}

		private TpeMirror makeArray(TpeMirror tpeMirror, int depth) {
			if (depth == 0) return tpeMirror;
			else return makeArray(new ArrayTpe(tpeMirror), depth - 1);
		}

		@Override
		public TpeMirror visit(WildcardType n, Scope arg) {
			ReferenceType eBound = n.getExtends();
			ReferenceType sBound = n.getSuper();

			TpeMirror eBoundMirror = eBound != null ? eBound.accept(this, arg) : typeUtils.objectType();
			TpeMirror sBoundMirror = sBound != null ? sBound.accept(this, arg) : NullTpe.NULL;
			return new WildcardTpe(eBoundMirror, sBoundMirror);
		}

		@Override
		public TpeMirror visit(PrimitiveType n, Scope arg) {
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
		public TpeMirror visit(VoidType n, Scope arg) {
			return NoTpe.VOID;
		}
	}

	class CycleDetectingTypeVisitor extends TypeVisitor {

		Map<TypeParameterElem, Object> pendingResolution = new IdentityHashMap<TypeParameterElem, Object>();

		@Override
		public void resolveBounds(TypeParameterElem typeParameterElem, Scope scope) {
			List<TpeMirror> boundsMirrors = typeParameterElem.getBounds();
			if (boundsMirrors == null) {
				SourceOrigin origin = (SourceOrigin) typeParameterElem.origin();
				TypeParameter node = (TypeParameter) origin.getNode();
				List<ClassOrInterfaceType> bounds = node.getTypeBound();

				if (pendingResolution.containsKey(typeParameterElem))
					throw new ScopeException("Circular type parameter definition", node);

				pendingResolution.put(typeParameterElem, new Object());
				boundsMirrors = visitAll(this, scope, bounds);
				typeParameterElem.setBounds(boundsMirrors);
				typeParameterElem.setType(boundsMirrors.isEmpty() ?
						new TpeVariable(typeParameterElem, typeUtils.objectType(), NullTpe.NULL) :
						new TpeVariable(typeParameterElem, new IntersectionTpe(boundsMirrors), NullTpe.NULL));
				pendingResolution.remove(typeParameterElem);
			}
		}
	}
}
