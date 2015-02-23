package com.github.javaparser.model.scope;

import javax.lang.model.element.Name;

/**
 * @author Didier Villevalois
 */
public abstract class EltName implements Name {

	private final String nameString;

	EltName(String nameString) {
		this.nameString = nameString;
	}

	public abstract boolean isQualified();

	public abstract EltName qualifier();

	public abstract EltSimpleName simpleName();

	public EltSimpleName rootQualifier() {
		return isQualified() ? qualifier().rootQualifier() : simpleName();
	}

	public abstract EltName withoutRoot();

	public boolean isEmpty() {
		return nameString.isEmpty();
	}

	@Override
	public boolean contentEquals(CharSequence charSequence) {
		return nameString.equals(charSequence);
	}

	@Override
	public int length() {
		return nameString.length();
	}

	@Override
	public char charAt(int index) {
		return nameString.charAt(index);
	}

	@Override
	public CharSequence subSequence(int start, int end) {
		return nameString.subSequence(start, end);
	}

	@Override
	public String toString() {
		return nameString;
	}

	@Override
	public int hashCode() {
		return nameString.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		}
		if (obj == null || !(obj instanceof EltName)) {
			return false;
		}
		return nameString.equals(((EltName) obj).nameString);
	}
}
