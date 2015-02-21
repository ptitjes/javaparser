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
