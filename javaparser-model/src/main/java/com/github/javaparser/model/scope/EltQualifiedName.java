package com.github.javaparser.model.scope;

/**
 * @author Didier Villevalois
 */
class EltQualifiedName extends EltName {

	private final EltName qualifier;
	private final EltName simple;

	EltQualifiedName(String nameString, EltName qualifier, EltName simple) {
		super(nameString);
		this.qualifier = qualifier;
		this.simple = simple;
	}

	@Override
	public boolean isQualified() {
		return true;
	}

	@Override
	public EltName getQualifier() {
		return qualifier;
	}

	@Override
	public EltName getSimpleName() {
		return simple;
	}
}
