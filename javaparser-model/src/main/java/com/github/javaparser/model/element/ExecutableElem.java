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

package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.ExecutableTpe;
import com.github.javaparser.model.type.TpeMirror;
import com.github.javaparser.model.type.TpeVariable;

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
	private TpeMirror receiverType;
	private TpeMirror returnType;
	private boolean isVarArgs;
	private List<TpeMirror> thrownTypes;
	private AnnotationValue defaultValue;

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
		List<TpeVariable> typeVariables = new ArrayList<TpeVariable>();
		for (TypeParameterElem typeParameter : typeParameters) {
			typeVariables.add((TpeVariable) typeParameter.asType());
		}
		List<TpeMirror> parameterTypes = new ArrayList<TpeMirror>();
		for (VariableElem parameter : parameters) {
			parameterTypes.add(parameter.asType());
		}
		return new ExecutableTpe(typeVariables, receiverType, parameterTypes, returnType, thrownTypes);
	}

	@Override
	public final List<TypeParameterElem> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public TypeMirror getReturnType() {
		return returnType;
	}

	public void setReturnType(TpeMirror returnType) {
		this.returnType = returnType;
	}

	@Override
	public final List<? extends VariableElement> getParameters() {
		return parameters;
	}

	@Override
	public TypeMirror getReceiverType() {
		return receiverType;
	}

	public void setReceiverType(TpeMirror receiverType) {
		this.receiverType = receiverType;
	}

	@Override
	public boolean isVarArgs() {
		return isVarArgs;
	}

	public void setVarArgs(boolean isVarArgs) {
		this.isVarArgs = isVarArgs;
	}

	@Override
	public boolean isDefault() {
		return getModifiers().contains(Modifier.DEFAULT);
	}

	@Override
	public List<? extends TypeMirror> getThrownTypes() {
		return thrownTypes;
	}

	public void setThrownTypes(List<TpeMirror> thrownTypes) {
		this.thrownTypes = thrownTypes;
	}

	@Override
	public AnnotationValue getDefaultValue() {
		return null;
	}

	public void setDefaultValue(AnnotationValue defaultValue) {
		this.defaultValue = defaultValue;
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
		public TypeParameterElem resolveLocalTypeParameter(EltSimpleName name) {
			return perNameTypeParameters.get(name);
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
