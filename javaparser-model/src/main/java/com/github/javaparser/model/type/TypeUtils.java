package com.github.javaparser.model.type;

import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.TypeParameterElem;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class TypeUtils implements Types {

	private final Analysis analysis;

	public TypeUtils(Analysis analysis) {
		this.analysis = analysis;
	}

	@Override
	public Element asElement(TypeMirror t) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public TypeMirror asMemberOf(DeclaredType containing, Element element) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean isSameType(TypeMirror t1, TypeMirror t2) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean isSubtype(TypeMirror t1, TypeMirror t2) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean isAssignable(TypeMirror t1, TypeMirror t2) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean contains(TypeMirror t1, TypeMirror t2) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean isSubsignature(ExecutableType m1, ExecutableType m2) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public List<? extends TypeMirror> directSupertypes(TypeMirror t) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public TypeMirror erasure(TypeMirror t) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public TypeElement boxedClass(PrimitiveType p) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public PrimitiveType unboxedType(TypeMirror t) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public TypeMirror capture(TypeMirror t) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public PrimitiveType getPrimitiveType(TypeKind kind) {
		switch (kind) {
			case BOOLEAN:
				return PrimitiveTpe.BOOLEAN;
			case BYTE:
				return PrimitiveTpe.BYTE;
			case SHORT:
				return PrimitiveTpe.SHORT;
			case INT:
				return PrimitiveTpe.INT;
			case LONG:
				return PrimitiveTpe.LONG;
			case CHAR:
				return PrimitiveTpe.CHAR;
			case FLOAT:
				return PrimitiveTpe.FLOAT;
			case DOUBLE:
				return PrimitiveTpe.DOUBLE;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public NullType getNullType() {
		return NullTpe.NULL;
	}

	@Override
	public NoType getNoType(TypeKind kind) {
		switch (kind) {
			case VOID:
				return NoTpe.VOID;
			case NONE:
				return NoTpe.NONE;
			default:
				throw new IllegalArgumentException();
		}
	}

	@Override
	public ArrayType getArrayType(TypeMirror componentType) {
		return new ArrayTpe(componentType);
	}

	@Override
	public WildcardType getWildcardType(TypeMirror extendsBound, TypeMirror superBound) {
		return new WildcardTpe((TpeMirror) extendsBound, (TpeMirror) superBound);
	}

	@Override
	public DeclaredType getDeclaredType(TypeElement typeElem, TypeMirror... typeArgs) {
		return new DeclaredTpe(NoTpe.NONE, (TypeElem) typeElem, Arrays.asList((TpeMirror[]) typeArgs));
	}

	@Override
	public DeclaredType getDeclaredType(DeclaredType containing, TypeElement typeElem, TypeMirror... typeArgs) {
		return new DeclaredTpe((TpeMirror) containing, (TypeElem) typeElem, Arrays.asList((TpeMirror[]) typeArgs));
	}
}
