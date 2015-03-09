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
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.VariableElem;

import java.util.List;

/**
 * @author Didier Villevalois
 */
public abstract class RootScope extends Scope {

	public abstract List<PackageElem> resolvePackages(EltName name);

	@Override
	public TypeElem resolveType(EltName name) {
		if (name.isEmpty())
			return null;

		if (name.isQualified()) {
			List<PackageElem> packageElems = resolvePackages(name.qualifier());
			if (packageElems != null) {
				for (PackageElem packageElem : packageElems) {
					TypeElem typeElem = packageElem.scope().resolveLocalType(name.simpleName());
					if (typeElem != null) return typeElem;
				}
			}

			TypeElem parentTypeElem = resolveType(name.qualifier());
			if (parentTypeElem != null) {
				TypeElem typeElem = parentTypeElem.scope().resolveType(name.simpleName());
				if (typeElem != null) return typeElem;
			}
		}

		return super.resolveType(name);
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
}
