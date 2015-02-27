package com.github.javaparser.model;

import com.github.javaparser.ast.Node;

/**
 * @author Didier Villevalois
 */
public class JavaAnalyserException extends RuntimeException {

	private final Node node;

	public JavaAnalyserException(String message) {
		super(message);
		this.node = null;
	}

	public JavaAnalyserException(String message, Node node) {
		super(message);
		this.node = node;
	}

	public JavaAnalyserException(String message, Exception e) {
		super(message, e);
		this.node = null;
	}

	public Node getNode() {
		return node;
	}
}
