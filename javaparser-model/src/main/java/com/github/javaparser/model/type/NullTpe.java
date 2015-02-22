package com.github.javaparser.model.type;

import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Didier Villevalois
 */
public class NullTpe extends TpeMirror implements NullType {

	public static final NullTpe NULL = new NullTpe();

	@Override
	public TypeKind getKind() {
		return TypeKind.NULL;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitNull(this, p);
	}

	@Override
	public String toString() {
		return "null";
	}
}
