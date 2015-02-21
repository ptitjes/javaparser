package com.github.javaparser.model.source.element;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public class SrcVariableElem extends SrcElem implements VariableElement {

	public SrcVariableElem(SrcFile source,
	                       Node node,
	                       SrcElem enclosing,
	                       Set<Modifier> modifiers,
	                       EltName simpleName,
	                       ElementKind kind) {
		super(source, node, enclosing, modifiers, simpleName, kind);
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return v.visitVariable(this, p);
	}

	@Override
	public TpeMirror asType() {
		return null;
	}

	@Override
	public Object getConstantValue() {
		return null;
	}
}
