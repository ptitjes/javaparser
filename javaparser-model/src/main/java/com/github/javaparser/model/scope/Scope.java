package com.github.javaparser.model.scope;

import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.VariableElem;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

/**
 * @author Didier Villevalois
 */
public abstract class Scope {

	public abstract Scope parentScope();

	public Scope rootScope() {
		if (parentScope() == null) return this;
		else return parentScope().rootScope();
	}

	// TODO Add visibility discrimination
	public TypeElem resolveType(EltName name) {
		TypeElem elem = null;

		if (name.isQualified()) {
			TypeElem rootTypeElem = resolveLocalType(name.rootQualifier());
			if (rootTypeElem != null) {
				elem = rootTypeElem.scope().resolveType(name.withoutRoot());
			}
		} else {
			elem = resolveLocalType(name.simpleName());
		}

		if (elem == null && parentScope() != null) {
			elem = parentScope().resolveType(name);
		}

		return elem;
	}

	// TODO Add visibility discrimination
	public VariableElem resolveVariable(EltName name) {
		VariableElem elem = null;

		if (name.isQualified()) {
			TypeElem rootTypeElem = resolveLocalType(name.rootQualifier());
			if (rootTypeElem != null) {
				elem = rootTypeElem.scope().resolveVariable(name.withoutRoot());
			}
		} else {
			elem = resolveLocalVariable(name.simpleName());
		}

		if (elem == null && parentScope() != null) {
			elem = parentScope().resolveVariable(name);
		}

		return elem;
	}

	// TODO Add visibility discrimination
	// TODO Add signature discrimination (parameters' type)
	public ExecutableElem resolveExecutable(EltName name) {
		ExecutableElem elem = null;

		if (name.isQualified()) {
			TypeElem rootTypeElem = resolveLocalType(name.rootQualifier());
			if (rootTypeElem != null) {
				elem = rootTypeElem.scope().resolveExecutable(name.withoutRoot());
			}
		} else {
			elem = resolveLocalExecutable(name.simpleName());
		}

		if (elem == null && parentScope() != null) {
			elem = parentScope().resolveExecutable(name);
		}

		return elem;
	}

	// TODO Add visibility discrimination
	public abstract TypeElem resolveLocalType(EltSimpleName name);

	// TODO Add visibility discrimination
	public abstract VariableElem resolveLocalVariable(EltSimpleName name);

	// TODO Add signature discrimination (parameters' type)
	public abstract ExecutableElem resolveLocalExecutable(EltSimpleName name);
}
