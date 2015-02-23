package com.github.javaparser.model.scope;

/**
 * @author Didier Villevalois
 */
public abstract class EltNames {

	// TODO Should we use CharSequence directly in names and have utils for dot search ?

	public static final EltSimpleName empty = new EltSimpleName("");

	public static EltSimpleName makeSimple(CharSequence name) {
		return makeSimple(name.toString());
	}

	public static EltSimpleName makeSimple(String name) {
		if (name.lastIndexOf('.') != -1)
			throw new IllegalArgumentException("Name is not simple");

		return new EltSimpleName(name);
	}

	public static EltName make(CharSequence name) {
		return make(name.toString());
	}

	public static EltName make(String name) {
		if (name.isEmpty()) {
			return empty;
		}

		int dotIndex = name.lastIndexOf('.');
		if (dotIndex == -1)
			return new EltSimpleName(name);

		return new EltQualifiedName(name,
				make(name.substring(0, dotIndex)),
				makeSimple(name.substring(dotIndex + 1)));
	}

	public static EltName make(EltName qualifier, CharSequence name) {
		return make(qualifier, name.toString());
	}

	public static EltName make(EltName qualifier, String name) {
		return new EltQualifiedName(qualifier.toString() + '.' + name,
				qualifier, makeSimple(name));
	}
}
