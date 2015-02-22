package com.github.javaparser.model.type;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Didier Villevalois
 */
public class TpeVariable extends TpeMirror implements TypeVariable {

	private final Element element;
	private final TpeMirror upperBound;
	private final TpeMirror lowerBound;

	public TpeVariable(Element element,
	                   TpeMirror upperBound,
	                   TpeMirror lowerBound) {
		this.element = element;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	@Override
	public Element asElement() {
		return element;
	}

	@Override
	public TypeMirror getUpperBound() {
		return upperBound;
	}

	@Override
	public TypeMirror getLowerBound() {
		return lowerBound;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.TYPEVAR;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitTypeVariable(this, p);
	}

	@Override
	public String toString() {
		// TODO add bounds
		return element.getSimpleName().toString();
	}
}
