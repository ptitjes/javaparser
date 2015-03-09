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

import javax.lang.model.element.Name;

/**
 * @author Didier Villevalois
 */
public abstract class EltName implements Name {

	private final String nameString;

	EltName(String nameString) {
		this.nameString = nameString;
	}

	public abstract boolean isQualified();

	public abstract EltName qualifier();

	public abstract EltSimpleName simpleName();

	public EltSimpleName rootQualifier() {
		return isQualified() ? qualifier().rootQualifier() : simpleName();
	}

	public abstract EltName withoutRoot();

	public boolean isEmpty() {
		return nameString.isEmpty();
	}

	@Override
	public boolean contentEquals(CharSequence charSequence) {
		return nameString.equals(charSequence);
	}

	@Override
	public int length() {
		return nameString.length();
	}

	@Override
	public char charAt(int index) {
		return nameString.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return nameString.subSequence(start, end);
	}

	@Override
	public String toString() {
		return nameString;
	}

	@Override
	public int hashCode() {
		return nameString.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || !(obj instanceof EltName)) {
			return false;
		}
		return nameString.equals(((EltName) obj).nameString);
	}
}
