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

	public abstract EltName getQualifier();

	public abstract EltName getSimpleName();

	public EltName getRootQualifier() {
		return isQualified() ? getQualifier().getRootQualifier() : getSimpleName();
	}

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
		return obj instanceof EltName && nameString.equals(((EltName) obj).nameString);
	}
}
