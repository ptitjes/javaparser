package com.github.javaparser.model.type;

import javax.lang.model.type.IntersectionType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class IntersectionTpe extends TpeMirror implements IntersectionType {

	private final List<TpeMirror> bounds;

	public IntersectionTpe(List<TpeMirror> bounds) {
		this.bounds = bounds;
	}

	@Override
	public List<? extends TypeMirror> getBounds() {
		return bounds;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.INTERSECTION;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitIntersection(this, p);
	}


	@Override
	public String toString() {
		return allToString(bounds, "&");
	}
}
