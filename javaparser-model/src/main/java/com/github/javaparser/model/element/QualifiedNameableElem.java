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

import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public abstract class QualifiedNameableElem extends Elem implements QualifiedNameable {

	private final EltName qualifiedName;

	public QualifiedNameableElem(Origin origin, Scope parentScope, Elem enclosing, Set<Modifier> modifiers,
	                             EltName qualifiedName, EltSimpleName simpleName, ElementKind kind) {
		super(origin, parentScope, enclosing, modifiers, simpleName, kind);
		this.qualifiedName = qualifiedName;
	}

	@Override
	public EltName getQualifiedName() {
		return qualifiedName;
	}
}
