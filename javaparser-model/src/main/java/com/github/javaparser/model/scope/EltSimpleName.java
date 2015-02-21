package com.github.javaparser.model.scope;

/**
 * @author Didier Villevalois
 */
class EltSimpleName extends EltName {

	EltSimpleName(String name) {
		super(name);
	}

	@Override
	public boolean isQualified() {
		return false;
	}

	@Override
	public EltName getSimpleName() {
		return this;
	}

	@Override
	public EltName getQualifier() {
		return null;
	}
}
