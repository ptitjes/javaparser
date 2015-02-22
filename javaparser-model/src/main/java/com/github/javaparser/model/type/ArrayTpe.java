package com.github.javaparser.model.type;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Didier Villevalois
 */
public class ArrayTpe extends TpeMirror implements ArrayType {

	private final TypeMirror componentType;

	public ArrayTpe(TypeMirror componentType) {
		this.componentType = componentType;
	}

	@Override
	public TypeMirror getComponentType() {
		return componentType;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.ARRAY;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitArray(this, p);
	}

	@Override
	public String toString() {
		return componentType.toString() + "[]";
	}
}
