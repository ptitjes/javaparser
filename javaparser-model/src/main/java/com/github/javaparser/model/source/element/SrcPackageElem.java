package com.github.javaparser.model.source.element;

import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.type.NoTpe;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import java.util.EnumSet;

/**
 * @author Didier Villevalois
 */
public class SrcPackageElem extends SrcElem implements PackageElement {

	private final EltName name;

	public SrcPackageElem(EltName name) {
		super(null, null, null, EnumSet.noneOf(Modifier.class), name, ElementKind.PACKAGE);
		this.name = name;
	}

	@Override
	public Name getQualifiedName() {
		return name;
	}

	@Override
	public boolean isUnnamed() {
		return name.length() == 0;
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> elementVisitor, P p) {
		return elementVisitor.visitPackage(this, p);
	}

	@Override
	public TpeMirror asType() {
		return NoTpe.PACKAGE;
	}
}
