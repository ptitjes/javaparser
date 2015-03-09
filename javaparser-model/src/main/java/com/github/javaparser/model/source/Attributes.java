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

package com.github.javaparser.model.source;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.scope.Scope;

/**
 * @author Didier Villevalois
 */
public class Attributes {

	protected static Attributes get(Node node) {
		return (Attributes) node.getData();
	}

	private static void set(Node node, Attributes data) {
		node.setData(data);
	}

	private final CompilationUnit compilationUnit;
	private final Node node;

	public Attributes(CompilationUnit compilationUnit, Node node) {
		this.compilationUnit = compilationUnit;
		this.node = node;
		if (node != null) Attributes.set(node, this);
	}

	public CompilationUnit compilationUnit() {
		return compilationUnit;
	}

	public Node node() {
		return node;
	}

	public Elem element() {
		return null;
	}

	public Scope scope() {
		return null;
	}
}
