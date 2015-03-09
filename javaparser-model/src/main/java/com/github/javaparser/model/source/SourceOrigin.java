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

import com.github.javaparser.Position;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.model.element.Origin;

/**
 * @author Didier Villevalois
 */
public class SourceOrigin implements Origin {

	private final CompilationUnit cu;
	private final Node node;

	public SourceOrigin(CompilationUnit cu, Node node) {
		this.cu = cu;
		this.node = node;
	}

	@Override
	public String toLocationString() {
		CompilationUnitAttr srcFile = CompilationUnitAttr.get(cu);
		Position position = Position.beginOf(node);
		return srcFile.file().getPath() + "(" + position.getLine() + "," + position.getColumn() + ")";
	}

	public CompilationUnit getCu() {
		return cu;
	}

	public Node getNode() {
		return node;
	}

	@Override
	public String toString() {
		PackageDeclaration packageDecl = cu.getPackage();
		return "SourceOrigin{package=" +
				(packageDecl == null ? "<default>" : packageDecl.getName()) +
				", nodeClass=" + node.getClass() + '}';
	}
}
