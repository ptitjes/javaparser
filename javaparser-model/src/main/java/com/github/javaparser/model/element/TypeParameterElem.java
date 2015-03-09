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
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class TypeParameterElem extends Elem implements TypeParameterElement {

	private List<TpeMirror> bounds;
	private TpeMirror type;

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
		return this.type;
	}

	public void setType(TpeMirror type) {
		this.type = type;
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
