package com.github.javaparser.model.type;

import com.github.javaparser.model.element.TypeElem;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class DeclaredTpe extends TpeMirror implements DeclaredType {

	private final TpeMirror enclosingType;
	private final TypeElem element;
	private final List<TpeMirror> typeArguments;

	public DeclaredTpe(TpeMirror enclosingType,
	                   TypeElem element,
	                   List<TpeMirror> typeArguments) {
		this.enclosingType = enclosingType;
		this.element = element;
		this.typeArguments = typeArguments;
	}

	@Override
	public TypeMirror getEnclosingType() {
		return enclosingType;
	}

	@Override
	public TypeElem asElement() {
		return element;
	}

	@Override
	public List<? extends TypeMirror> getTypeArguments() {
		return typeArguments;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.DECLARED;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitDeclared(this, p);
	}

	@Override
	public String toString() {
		return (enclosingType != NoTpe.NONE ? enclosingType + "." : "") +
				"(" + element.getQualifiedName() + ")" + element.getSimpleName() + "<" + allToString(typeArguments) + ">";
	}
}
