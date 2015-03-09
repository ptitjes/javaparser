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

package com.github.javaparser.model.source.utils;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public abstract class NodeListUtils {

	public static <E> List<E> safeIterable(List<E> iterableOrNull) {
		if (iterableOrNull == null) {
			return Collections.emptyList();
		} else return iterableOrNull;
	}

	public static <E extends Node, A> void visitAll(VoidVisitor<A> visitor, A arg, List<E> iterableOrNull) {
		if (iterableOrNull == null) {
			return;
		}

		for (E node : safeIterable(iterableOrNull)) {
			node.accept(visitor, arg);
		}
	}

	public static <E extends Node, R, A> List<R> visitAll(GenericVisitor<R, A> visitor, A arg, List<E> iterableOrNull) {
		if (iterableOrNull == null) {
			return Collections.emptyList();
		}

		List<R> results = new ArrayList<R>();
		for (E node : safeIterable(iterableOrNull)) {
			results.add(node.accept(visitor, arg));
		}
		return results;
	}
}
