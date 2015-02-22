package com.github.javaparser.model.type;

import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.UnionType;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class UnionTpe extends TpeMirror implements UnionType {

	private final List<TpeMirror> alternatives;

	public UnionTpe(List<TpeMirror> alternatives) {
		this.alternatives = alternatives;
	}

	@Override
	public List<? extends TypeMirror> getAlternatives() {
		return alternatives;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.UNION;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitUnion(this, p);
	}


	@Override
	public String toString() {
		return allToString(alternatives, "&");
	}
}
