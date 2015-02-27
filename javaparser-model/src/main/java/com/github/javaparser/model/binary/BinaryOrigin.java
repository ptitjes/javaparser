package com.github.javaparser.model.binary;

import com.github.javaparser.model.element.Origin;
import com.github.javaparser.model.scope.EltName;

/**
 * @author Didier Villevalois
 */
public class BinaryOrigin implements Origin {

	public BinaryOrigin(EltName qualifiedName) {
	}

	@Override
	public String toLocationString() {
		return null;
	}
}
