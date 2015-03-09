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

package com.github.javaparser.model.scope;

import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.TypeParameterElem;
import com.github.javaparser.model.element.VariableElem;

/**
 * @author Didier Villevalois
 */
public abstract class Scope {

	public abstract Scope parentScope();

	// TODO Add visibility discrimination
	public TypeParameterElem resolveTypeParameter(EltSimpleName name) {
		TypeParameterElem elem = resolveLocalTypeParameter(name);

		if (elem == null && parentScope() != null) {
			elem = parentScope().resolveTypeParameter(name);
		}

		return elem;
	}

	// TODO Add visibility discrimination
	public TypeElem resolveType(EltName name) {
		TypeElem elem = null;

		if (name.isQualified()) {
			TypeElem rootTypeElem = resolveLocalType(name.rootQualifier());
			if (rootTypeElem != null) {
				elem = rootTypeElem.scope().resolveType(name.withoutRoot());
			}
		} else {
			elem = resolveLocalType(name.simpleName());
		}

		if (elem == null && parentScope() != null) {
			elem = parentScope().resolveType(name);
		}

		return elem;
	}

	// TODO Add visibility discrimination
	public VariableElem resolveVariable(EltName name) {
		VariableElem elem = null;

		if (name.isQualified()) {
			TypeElem rootTypeElem = resolveLocalType(name.rootQualifier());
			if (rootTypeElem != null) {
				elem = rootTypeElem.scope().resolveVariable(name.withoutRoot());
			}
		} else {
			elem = resolveLocalVariable(name.simpleName());
		}

		if (elem == null && parentScope() != null) {
			elem = parentScope().resolveVariable(name);
		}

		return elem;
	}

	// TODO Add visibility discrimination
	// TODO Add signature discrimination (parameters' type)
	public ExecutableElem resolveExecutable(EltName name) {
		ExecutableElem elem = null;

		if (name.isQualified()) {
			TypeElem rootTypeElem = resolveLocalType(name.rootQualifier());
			if (rootTypeElem != null) {
				elem = rootTypeElem.scope().resolveExecutable(name.withoutRoot());
			}
		} else {
			elem = resolveLocalExecutable(name.simpleName());
		}

		if (elem == null && parentScope() != null) {
			elem = parentScope().resolveExecutable(name);
		}

		return elem;
	}

	// TODO Add visibility discrimination
	public TypeParameterElem resolveLocalTypeParameter(EltSimpleName name) {
		return null;
	}

	// TODO Add visibility discrimination
	public abstract TypeElem resolveLocalType(EltSimpleName name);

	// TODO Add visibility discrimination
	public abstract VariableElem resolveLocalVariable(EltSimpleName name);

	// TODO Add signature discrimination (parameters' type)
	public abstract ExecutableElem resolveLocalExecutable(EltSimpleName name);
}
