package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class ExecutableElem extends Elem implements ExecutableElement {

	private final List<TypeParameterElem> typeParameters = new ArrayList<TypeParameterElem>();
	private final Map<EltSimpleName, TypeParameterElem> perNameTypeParameters = new HashMap<EltSimpleName, TypeParameterElem>();
	private final List<VariableElem> parameters = new ArrayList<VariableElem>();
	private final Map<EltSimpleName, VariableElem> perNameParam = new HashMap<EltSimpleName, VariableElem>();

	public ExecutableElem(Origin origin,
	                      Elem enclosing,
	                      Set<Modifier> modifiers,
	                      EltSimpleName simpleName,
	                      ElementKind kind) {
		super(origin, enclosing.scope(), enclosing, modifiers, simpleName, kind);
	}

	@Override
	protected void addEnclosedElem(Elem elem) {
		EltSimpleName name = elem.getSimpleName();

		switch (elem.getKind()) {
			case TYPE_PARAMETER:
				typeParameters.add((TypeParameterElem) elem);
				perNameTypeParameters.put(name, (TypeParameterElem) elem);
				break;
			case PARAMETER:
				parameters.add((VariableElem) elem);
				perNameParam.put(name, (VariableElem) elem);
				break;
		}
	}

	@Override
	public final <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return v.visitExecutable(this, p);
	}

	@Override
	public TpeMirror asType() {
		return null;
	}

	@Override
	public final List<? extends TypeParameterElement> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public TypeMirror getReturnType() {
		return null;
	}

	@Override
	public final List<? extends VariableElement> getParameters() {
		return parameters;
	}

	@Override
	public TypeMirror getReceiverType() {
		return null;
	}

	@Override
	public boolean isVarArgs() {
		return false;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public List<? extends TypeMirror> getThrownTypes() {
		return null;
	}

	@Override
	public AnnotationValue getDefaultValue() {
		return null;
	}

	@Override
	public Scope scope() {
		return scope;
	}

	private Scope scope = new Scope() {
		@Override
		public Scope parentScope() {
			return ExecutableElem.this.parentScope();
		}

		@Override
		public TypeElem resolveLocalType(EltSimpleName name) {
			return null;
		}

		@Override
		public VariableElem resolveLocalVariable(EltSimpleName name) {
			return perNameParam.get(name);
		}

		@Override
		public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
			return null;
		}
	};
}
