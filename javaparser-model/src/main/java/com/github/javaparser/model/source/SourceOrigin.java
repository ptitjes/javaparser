package com.github.javaparser.model.source;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
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
}
