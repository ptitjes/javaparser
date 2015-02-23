package com.github.javaparser.model.type;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.element.ElementUtils;
import com.github.javaparser.model.element.TypeElem;

import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.*;
import javax.lang.model.util.Types;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class TypeUtils implements Types, Registry.Participant {

	private Classpath classpath;
	private ElementUtils elementUtils;

	@Override
	public void configure(Registry registry) {
		classpath = registry.get(Classpath.class);
		elementUtils = registry.get(ElementUtils.class);
	}

	/* Internal convenience API */

	public TpeMirror objectType() {
		TypeElem objectTypeElem = elementUtils.java_lang_Object();
		return new DeclaredTpe(NoTpe.NONE, objectTypeElem, Collections.<TpeMirror>emptyList());
	}

	public DeclaredTpe enumTypeOf(TpeMirror tpeMirror) {
		TypeElem enumTypeElem = elementUtils.java_lang_Enum();
		return new DeclaredTpe(NoTpe.NONE, enumTypeElem, Collections.singletonList(tpeMirror));
	}

	public DeclaredTpe annotationType() {
		TypeElem annotationTypeElem = elementUtils.java_lang_annotation_Annotation();
		return new DeclaredTpe(NoTpe.NONE, annotationTypeElem, Collections.<TpeMirror>emptyList());
	}

	/* Standard API */

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
