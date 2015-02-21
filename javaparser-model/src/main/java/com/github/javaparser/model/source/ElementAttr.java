package com.github.javaparser.model.source;

import com.github.javaparser.ast.Node;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.element.Elem;

/**
 * @author Didier Villevalois
 */
public class ElementAttr<E extends Elem> extends Attributes {

	private final E elem;

	public ElementAttr(SrcFile source, Node node, E elem) {
		super(source, node);
		this.elem = elem;
	}

	@Override
	public Elem definedElement() {
		return elem;
	}
}
