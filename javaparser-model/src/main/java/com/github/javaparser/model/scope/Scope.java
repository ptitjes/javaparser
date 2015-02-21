package com.github.javaparser.model.scope;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Didier Villevalois
 */
public abstract class Scope {

	private final Scope enclosingScope;

	public Scope(Scope enclosingScope) {
		this.enclosingScope = enclosingScope;
	}

	public Scope rootScope() {
		if (enclosingScope == null) return this;
		else return enclosingScope.rootScope();
	}

	// TODO Add visibility discrimination
	public TypeElement resolveType(EltName name) {
		TypeElement elem = resolveLocalType(name);
		if (elem == null) {
			elem = enclosingScope.resolveType(name);
		}
		return elem;
	}

	// TODO Add visibility discrimination
	public VariableElement resolveVariable(EltName name) {
		VariableElement elem = resolveLocalVariable(name);
		if (elem == null) {
			elem = enclosingScope.resolveVariable(name);
		}
		return elem;
	}

	// TODO Add visibility discrimination
	// TODO Add signature discrimination (parameters' type)
	public ExecutableElement resolveExecutable(EltName name) {
		ExecutableElement elem = resolveLocalExecutable(name);
		if (elem == null) {
			elem = enclosingScope.resolveExecutable(name);
		}
		return elem;
	}

	// TODO Add visibility discrimination
	public abstract TypeElement resolveLocalType(EltName name);

	// TODO Add visibility discrimination
	public abstract VariableElement resolveLocalVariable(EltName name);

	// TODO Add signature discrimination (parameters' type)
	public abstract ExecutableElement resolveLocalExecutable(EltName name);
}
