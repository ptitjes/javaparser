package com.github.javaparser.model.type;

import javax.lang.model.type.*;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class ExecutableTpe extends TpeMirror implements ExecutableType {

	private final List<TpeVariable> typeVariables;
	private final TpeMirror receiverType;
	private final List<TpeMirror> parameterTypes;
	private final TpeMirror returnType;
	private final List<TpeMirror> thrownTypes;

	public ExecutableTpe(List<TpeVariable> typeVariables,
	                     TpeMirror receiverType,
	                     List<TpeMirror> parameterTypes,
	                     TpeMirror returnType,
	                     List<TpeMirror> thrownTypes) {
		this.typeVariables = typeVariables;
		this.receiverType = receiverType;
		this.returnType = returnType;
		this.parameterTypes = parameterTypes;
		this.thrownTypes = thrownTypes;
	}

	@Override
	public List<? extends TypeVariable> getTypeVariables() {
		return typeVariables;
	}

	@Override
	public List<? extends TypeMirror> getParameterTypes() {
		return parameterTypes;
	}

	@Override
	public TypeMirror getReturnType() {
		return returnType;
	}

	@Override
	public List<? extends TypeMirror> getThrownTypes() {
		return thrownTypes;
	}

	@Override
	public TypeMirror getReceiverType() {
		return receiverType;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.EXECUTABLE;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitExecutable(this, p);
	}

	@Override
	public String toString() {
		return "<" + allToString(typeVariables) + ">" +
				returnType + " " + getReceiverType() + "." +
				null + "(" + allToString(parameterTypes) +
				" throws " + allToString(thrownTypes);
	}
}
