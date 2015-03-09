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

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public abstract class Elem implements Element {

	private final Origin origin;
	private final Scope parentScope;
	private final Elem enclosing;
	private final Set<Modifier> modifiers;
	private final EltSimpleName simpleName;
	private final ElementKind kind;
	protected final List<Elem> enclosed = new ArrayList<Elem>();

	public Elem(Origin origin, Scope parentScope, Elem enclosing, Set<Modifier> modifiers, EltSimpleName simpleName, ElementKind kind) {
		this.origin = origin;
		this.parentScope = parentScope;
		this.enclosing = enclosing;
		this.modifiers = modifiers;
		this.simpleName = simpleName;
		this.kind = kind;

		if (this.enclosing != null) this.enclosing.addEnclosedElem(this);
	}

	public final Origin origin() {
		return origin;
	}

	public final Scope parentScope() {
		return parentScope;
	}

	public abstract Scope scope();

	protected void addEnclosedElem(Elem elem) {
	}

	@Override
	public final Elem getEnclosingElement() {
		return enclosing;
	}

	@Override
	public final Set<Modifier> getModifiers() {
		return modifiers;
	}

	@Override
	public final EltSimpleName getSimpleName() {
		return simpleName;
	}

	@Override
	public final ElementKind getKind() {
		return kind;
	}

	@Override
	public final List<Elem> getEnclosedElements() {
		return enclosed;
	}

	@Override
	public abstract TpeMirror asType();

	@Override
	public List<? extends AnnotationMirror> getAnnotationMirrors() {
		return null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}

	@Override
	public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
		return null;
	}

	@Override
	public String toString() {
		return getKind() + " '" + getSimpleName() + "'";
	}
}
