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
public class EltQualifiedName extends EltName {

	private final EltName qualifier;
	private final EltSimpleName simple;

	EltQualifiedName(String nameString, EltName qualifier, EltSimpleName simple) {
		super(nameString);
		this.qualifier = qualifier;
		this.simple = simple;
	}

	@Override
	public boolean isQualified() {
		return true;
	}

	@Override
	public EltName qualifier() {
		return qualifier;
	}

	@Override
	public EltSimpleName simpleName() {
		return simple;
	}

	@Override
	public EltName withoutRoot() {
		EltName qualifierWithoutRoot = qualifier.isQualified() ? qualifier.withoutRoot() : EltNames.empty;
		return new EltQualifiedName(
				qualifierWithoutRoot.toString() + '.' + simple.toString(),
				qualifierWithoutRoot, simple);
	}
}
