package com.github.javaparser.model.source.element;

import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.EnumSet;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class SrcTypeParameterElem extends SrcElem implements TypeParameterElement {

	public SrcTypeParameterElem(SrcFile source,
	                            TypeParameter node,
	                            SrcElem enclosing,
	                            EltName simpleName) {
		super(source, node, enclosing, EnumSet.noneOf(Modifier.class), simpleName, ElementKind.TYPE_PARAMETER);
	}

	@Override
	public Element getGenericElement() {
		return getEnclosingElement();
	}

	@Override
	public List<? extends TypeMirror> getBounds() {
		return null;
	}

	@Override
	public TpeMirror asType() {
		return null;
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return v.visitTypeParameter(this, p);
	}
}
