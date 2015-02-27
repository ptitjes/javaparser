package com.github.javaparser.model.binary;

import com.github.javaparser.model.element.Origin;

/**
 * @author Didier Villevalois
 */
public class BinaryOrigin implements Origin {

	private final String internalName;

	public BinaryOrigin(String internalName) {
		this.internalName = internalName;
	}

	@Override
	public String toLocationString() {
		return null;
	}
}
