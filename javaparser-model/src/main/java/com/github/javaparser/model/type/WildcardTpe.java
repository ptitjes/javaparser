package com.github.javaparser.model.type;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.WildcardType;

/**
 * @author Didier Villevalois
 */
public class WildcardTpe extends TpeMirror implements WildcardType {

	private final TpeMirror extendsBound;
	private final TpeMirror superBound;

	public WildcardTpe(TpeMirror extendsBound, TpeMirror superBound) {
		this.extendsBound = extendsBound;
		this.superBound = superBound;
	}

	@Override
	public TypeMirror getExtendsBound() {
		return extendsBound;
	}

	@Override
	public TypeMirror getSuperBound() {
		return superBound;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.WILDCARD;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitWildcard(this, p);
	}

	@Override
	public String toString() {
		// TODO add bounds
		return "?";
	}
}
