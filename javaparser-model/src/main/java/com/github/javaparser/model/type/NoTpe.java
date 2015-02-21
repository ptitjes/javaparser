package com.github.javaparser.model.type;

import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Didier Villevalois
 */
public class NoTpe extends TpeMirror implements NoType {

	public static NoTpe NONE = new NoTpe(TypeKind.NONE);
	public static NoTpe VOID = new NoTpe(TypeKind.VOID);
	public static NoTpe PACKAGE = new NoTpe(TypeKind.PACKAGE);

	private final TypeKind kind;

	private NoTpe(TypeKind kind) {
		this.kind = kind;
	}

	@Override
	public TypeKind getKind() {
		return kind;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitNoType(this, p);
	}
}
