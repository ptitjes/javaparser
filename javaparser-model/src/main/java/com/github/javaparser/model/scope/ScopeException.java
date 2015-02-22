package com.github.javaparser.model.scope;

import com.github.javaparser.ast.Node;
import com.github.javaparser.model.JavaAnalyserException;

/**
 * @author Didier Villevalois
 */
public class ScopeException extends JavaAnalyserException {

	public ScopeException(String message, Node node) {
		super(message, node);
	}
}
