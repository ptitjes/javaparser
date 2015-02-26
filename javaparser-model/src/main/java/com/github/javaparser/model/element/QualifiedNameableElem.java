package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.QualifiedNameable;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public abstract class QualifiedNameableElem extends Elem implements QualifiedNameable {

	private final EltName qualifiedName;

	public QualifiedNameableElem(Origin origin, Scope parentScope, Elem enclosing, Set<Modifier> modifiers,
	                             EltName qualifiedName, EltSimpleName simpleName, ElementKind kind) {
		super(origin, parentScope, enclosing, modifiers, simpleName, kind);
		this.qualifiedName = qualifiedName;
	}

	@Override
	public EltName getQualifiedName() {
		return qualifiedName;
	}
}
