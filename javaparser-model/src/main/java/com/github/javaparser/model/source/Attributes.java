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
