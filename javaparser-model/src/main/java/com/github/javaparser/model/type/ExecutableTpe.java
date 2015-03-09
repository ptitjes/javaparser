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
		StringBuilder buffer = new StringBuilder();
		if (!typeVariables.isEmpty()) {
			buffer.append('<');
			buffer.append(allToString(typeVariables));
			buffer.append('>');
			buffer.append(' ');
		}
		buffer.append(returnType);
		buffer.append(' ');
		buffer.append('(');
		buffer.append(allToString(parameterTypes));
		buffer.append(')');
		if (!thrownTypes.isEmpty()) {
			buffer.append(" throws ");
			buffer.append(allToString(thrownTypes));
		}
		return buffer.toString();
	}
}
