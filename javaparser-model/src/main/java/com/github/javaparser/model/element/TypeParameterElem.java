package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class TypeParameterElem extends Elem implements TypeParameterElement {

	private List<TpeMirror> bounds;

	public TypeParameterElem(Origin origin,
	                         Elem enclosing,
	                         EltSimpleName simpleName) {
		super(origin, enclosing.scope(), enclosing, EnumSet.noneOf(Modifier.class), simpleName, ElementKind.TYPE_PARAMETER);
	}

	@Override
	public final Element getGenericElement() {
		return getEnclosingElement();
	}

	@Override
	public final <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return v.visitTypeParameter(this, p);
	}

	@Override
	public TpeMirror asType() {
		return null;
	}

	@Override
	public List<TpeMirror> getBounds() {
		return bounds;
	}

	public void setBounds(List<TpeMirror> bounds) {
		this.bounds = bounds;
	}

	@Override
	public Scope scope() {
		return scope;
	}

	private Scope scope = new Scope() {
		@Override
		public Scope parentScope() {
			return TypeParameterElem.this.parentScope();
		}

		@Override
		public TypeElem resolveLocalType(EltSimpleName name) {
			return null;
		}

		@Override
		public VariableElem resolveLocalVariable(EltSimpleName name) {
			return null;
		}

		@Override
		public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
			return null;
		}
	};
}
