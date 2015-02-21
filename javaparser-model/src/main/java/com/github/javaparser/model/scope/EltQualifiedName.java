package com.github.javaparser.model.scope;

/**
 * @author Didier Villevalois
 */
public class EltQualifiedName extends EltName {

	private final EltName qualifier;
	private final EltSimpleName simple;

	EltQualifiedName(String nameString, EltName qualifier, EltSimpleName simple) {
		super(nameString);
		this.qualifier = qualifier;
		this.simple = simple;
	}

	@Override
	public boolean isQualified() {
		return true;
	}

	@Override
	public EltName qualifier() {
		return qualifier;
	}

	@Override
	public EltSimpleName simpleName() {
		return simple;
	}

	@Override
	public EltName withoutRoot() {
		EltName qualifierWithoutRoot = qualifier.withoutRoot();
		return new EltQualifiedName(
				qualifierWithoutRoot.toString() + '.' + simple.toString(),
				qualifierWithoutRoot, simple);
	}
}
