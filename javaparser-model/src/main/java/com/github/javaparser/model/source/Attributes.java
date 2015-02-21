package com.github.javaparser.model.source;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.StringLiteralExpr;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.scope.Scope;

/**
 * @author Didier Villevalois
 */
public class Attributes {

	private static Attributes get(Node node) {
		return (Attributes) node.getData();
	}

	private static void set(Node node, Attributes data) {
		node.setData(data);
	}

	private final SrcFile source;
	private final Node node;

	public Attributes(SrcFile source, Node node) {
		this.source = source;
		this.node = node;
		if (node != null) Attributes.set(node, this);
	}

	public SrcFile source() {
		return source;
	}

	public Node node() {
		return node;
	}

	public Elem definedElement() {
		return null;
	}

	public Scope definedScope() {
		return null;
	}
}
