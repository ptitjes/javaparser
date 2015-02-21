package com.github.javaparser.model.source.element;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public class SrcExecutableElem extends SrcElem implements ExecutableElement {

	private final List<SrcTypeParameterElem> typeParameters = new ArrayList<SrcTypeParameterElem>();
	private final List<SrcVariableElem> parameters = new ArrayList<SrcVariableElem>();

	public SrcExecutableElem(SrcFile source,
	                         Node node,
	                         SrcElem enclosing,
	                         Set<Modifier> modifiers,
	                         EltName simpleName,
	                         ElementKind kind) {
		super(source, node, enclosing, modifiers, simpleName, kind);
	}

	@Override
	protected void addEnclosedElem(SrcElem elem) {
		super.addEnclosedElem(elem);
		switch (elem.getKind()) {
			case TYPE_PARAMETER:
				typeParameters.add((SrcTypeParameterElem) elem);
				break;
			case PARAMETER:
				parameters.add((SrcVariableElem) elem);
				break;
		}
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return v.visitExecutable(this, p);
	}

	@Override
	public TpeMirror asType() {
		return null;
	}

	@Override
	public List<? extends TypeParameterElement> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public TypeMirror getReturnType() {
		return null;
	}

	@Override
	public List<? extends VariableElement> getParameters() {
		return parameters;
	}

	@Override
	public TypeMirror getReceiverType() {
		return null;
	}

	@Override
	public boolean isVarArgs() {
		return false;
	}

	@Override
	public boolean isDefault() {
		return false;
	}

	@Override
	public List<? extends TypeMirror> getThrownTypes() {
		return null;
	}

	@Override
	public AnnotationValue getDefaultValue() {
		return null;
	}
}
