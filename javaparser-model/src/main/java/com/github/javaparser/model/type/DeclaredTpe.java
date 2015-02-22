package com.github.javaparser.model.type;

import javax.lang.model.element.Element;
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
	private final Element element;
	private final List<TpeMirror> typeArguments;

	public DeclaredTpe(TpeMirror enclosingType,
	                   Element element,
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
	public Element asElement() {
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
		return enclosingType + "." + element.getSimpleName() + "<" + allToString(typeArguments) + ">";
	}
}
