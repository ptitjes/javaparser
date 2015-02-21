package com.github.javaparser.model.scope;

/**
 * @author Didier Villevalois
 */
public class EltSimpleName extends EltName {

	EltSimpleName(String name) {
		super(name);
	}

	@Override
	public boolean isQualified() {
		return false;
	}

	@Override
	public EltSimpleName simpleName() {
		return this;
	}

	@Override
	public EltName qualifier() {
		return null;
	}

	@Override
	public EltName withoutRoot() {
		throw new IllegalArgumentException("Name is not qualified");
	}
}
