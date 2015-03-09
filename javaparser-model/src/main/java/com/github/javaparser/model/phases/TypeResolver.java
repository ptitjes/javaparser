/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2015 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaParser.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.javaparser.model.phases;

import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.type.*;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.TypeParameterElem;
import com.github.javaparser.model.scope.*;
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
					EltName maybePackageName = maybePackageName(typeScope);
					if (maybePackageName != null) {
						TypeElem typeElem = arg.resolveType(EltNames.make(maybePackageName, typeName));
						if (typeElem != null)
							return new DeclaredTpe(NoTpe.NONE, typeElem, tpeArgsMirrors);
					}

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

		private EltName maybePackageName(ClassOrInterfaceType type) {
			List<Type> typeArgs = type.getTypeArgs();
			if (typeArgs != null && !typeArgs.isEmpty()) return null;

			ClassOrInterfaceType typeScope = type.getScope();
			String typeName = type.getName();
			if (typeScope != null) {
				EltName qualifier = maybePackageName(typeScope);
				return qualifier == null ? null : EltNames.make(qualifier, typeName);
			} else return EltNames.makeSimple(typeName);
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

		Map<TypeParameterElem, Object> cycleMarkers = new IdentityHashMap<TypeParameterElem, Object>();
		Map<TypeParameterElem, Object> pendingResolution = new IdentityHashMap<TypeParameterElem, Object>();

		@Override
		public void resolveBounds(TypeParameterElem typeParameterElem, Scope scope) {
			if (pendingResolution.containsKey(typeParameterElem)) return;

			List<TpeMirror> boundsMirrors = typeParameterElem.getBounds();
			if (boundsMirrors == null) {
				SourceOrigin origin = (SourceOrigin) typeParameterElem.origin();
				TypeParameter node = (TypeParameter) origin.getNode();
				List<ClassOrInterfaceType> bounds = node.getTypeBound();

				if (cycleMarkers.containsKey(typeParameterElem))
					throw new ScopeException("Cyclic inheritance involving " +
							typeParameterElem.getSimpleName(), node);

				typeParameterElem.setType(new TpeVariable(typeParameterElem, typeUtils.objectType()));

				boundsMirrors = new ArrayList<TpeMirror>();
				for (ClassOrInterfaceType bound : bounds) {
					ClassOrInterfaceType typeScope = bound.getScope();
					EltSimpleName typeName = EltNames.makeSimple(bound.getName());
					List<Type> typeArgs = bound.getTypeArgs();
					if (typeScope == null && (typeArgs == null || typeArgs.isEmpty())) {
						TypeParameterElem otherTypeParameter = scope.resolveTypeParameter(typeName);
						if (otherTypeParameter != null) {
							cycleMarkers.put(typeParameterElem, new Object());
							resolveBounds(otherTypeParameter, scope);
							cycleMarkers.remove(typeParameterElem);
							boundsMirrors.add(otherTypeParameter.asType());
							continue;
						}
					}

					pendingResolution.put(typeParameterElem, new Object());
					boundsMirrors.add(bound.accept(this, scope));
					pendingResolution.remove(typeParameterElem);
				}
				typeParameterElem.setBounds(boundsMirrors);
			}
		}
	}
}
