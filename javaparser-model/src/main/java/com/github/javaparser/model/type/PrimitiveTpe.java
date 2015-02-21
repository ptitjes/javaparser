package com.github.javaparser.model.type;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Didier Villevalois
 */
public class PrimitiveTpe extends TpeMirror implements PrimitiveType {

	public static PrimitiveTpe BOOLEAN = new PrimitiveTpe(TypeKind.BOOLEAN);
	public static PrimitiveTpe BYTE = new PrimitiveTpe(TypeKind.BYTE);
	public static PrimitiveTpe SHORT = new PrimitiveTpe(TypeKind.SHORT);
	public static PrimitiveTpe INT = new PrimitiveTpe(TypeKind.INT);
	public static PrimitiveTpe LONG = new PrimitiveTpe(TypeKind.LONG);
	public static PrimitiveTpe CHAR = new PrimitiveTpe(TypeKind.CHAR);
	public static PrimitiveTpe FLOAT = new PrimitiveTpe(TypeKind.FLOAT);
	public static PrimitiveTpe DOUBLE = new PrimitiveTpe(TypeKind.DOUBLE);

	private final TypeKind kind;

	private PrimitiveTpe(TypeKind kind) {
		this.kind = kind;
	}

	@Override
	public TypeKind getKind() {
		return kind;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitPrimitive(this, p);
	}
}
