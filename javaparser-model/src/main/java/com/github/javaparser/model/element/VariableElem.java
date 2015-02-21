package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.VariableElement;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public class VariableElem extends Elem implements VariableElement {

	public VariableElem(Origin origin,
	                    Elem enclosing,
	                    Set<Modifier> modifiers,
	                    EltSimpleName simpleName,
	                    ElementKind kind) {
		super(origin, enclosing.scope(), enclosing, modifiers, simpleName, kind);
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return v.visitVariable(this, p);
	}

	@Override
	public TpeMirror asType() {
		return null;
	}

	@Override
	public Object getConstantValue() {
		return null;
	}

	@Override
	public Scope scope() {
		return scope;
	}

	private Scope scope = new Scope() {
		@Override
		public Scope parentScope() {
			return VariableElem.this.parentScope();
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
