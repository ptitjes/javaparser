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

/**
 * @author Didier Villevalois
 */
public abstract class EltNames {

	// TODO Should we use CharSequence directly in names and have utils for dot search ?

	public static final EltSimpleName empty = new EltSimpleName("");

	public static EltSimpleName makeSimple(CharSequence name) {
		return makeSimple(name.toString());
	}

	public static EltSimpleName makeSimple(String name) {
		if (name.lastIndexOf('.') != -1)
			throw new IllegalArgumentException("Name is not simple");

		return new EltSimpleName(name);
	}

	public static EltName make(CharSequence name) {
		return make(name.toString());
	}

	public static EltName make(String name) {
		if (name.isEmpty()) {
			return empty;
		}

		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1)
			return new EltSimpleName(name);

		return new EltQualifiedName(name,
				make(name.substring(0, dotIndex)),
				makeSimple(name.substring(dotIndex + 1)));
	}

	public static EltName make(EltName qualifier, CharSequence name) {
		return make(qualifier, makeSimple(name));
	}

	public static EltName make(EltName qualifier, String name) {
		return make(qualifier, makeSimple(name));
	}

	public static EltName make(EltName qualifier, EltSimpleName name) {
		return new EltQualifiedName(qualifier.toString() + '.' + name.toString(), qualifier, name);
	}
}
