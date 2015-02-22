package com.github.javaparser.model.source;

import com.github.javaparser.ast.Node;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.scope.Scope;

/**
 * @author Didier Villevalois
 */
public class ElementAttr<E extends Elem> extends Attributes {

	private final E elem;

	public ElementAttr(CompilationUnitAttr source, Node node, E elem) {
		super(source, node);
		this.elem = elem;
	}

	@Override
	public Elem element() {
		return elem;
	}

	@Override
	public Scope scope() {
		return elem.scope();
	}
}
