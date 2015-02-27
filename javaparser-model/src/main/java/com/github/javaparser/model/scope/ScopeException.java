package com.github.javaparser.model.scope;

import com.github.javaparser.ast.Node;
import com.github.javaparser.model.JavaAnalyserException;

import java.io.IOException;

/**
 * @author Didier Villevalois
 */
public class ScopeException extends JavaAnalyserException {

	public ScopeException(String message) {
		super(message);
	}

	public ScopeException(String message, Node node) {
		super(message, node);
	}

	public ScopeException(String message, Exception e) {
		super(message, e);
	}
}
