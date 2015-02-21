package com.github.javaparser.model.source.element;

import com.github.javaparser.ast.Node;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.source.Attributes;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public abstract class SrcElem extends Attributes implements Element {

	private final SrcElem enclosing;
	private final Set<Modifier> modifiers;
	private final EltName simpleName;
	private final ElementKind kind;
	private final List<SrcElem> enclosed = new ArrayList<SrcElem>();

	public SrcElem(SrcFile source, Node node, SrcElem enclosing, Set<Modifier> modifiers, EltName simpleName, ElementKind kind) {
		super(source, node);

		this.enclosing = enclosing;
		this.modifiers = modifiers;
		this.simpleName = simpleName;
		this.kind = kind;

		if (this.enclosing != null) this.enclosing.addEnclosedElem(this);
	}

	protected void addEnclosedElem(SrcElem elem) {
		enclosed.add(elem);
	}

	@Override
	public SrcElem definedElement() {
		return this;
	}

	@Override
	public SrcElem getEnclosingElement() {
		return enclosing;
	}

	@Override
	public Set<Modifier> getModifiers() {
		return modifiers;
	}

	@Override
	public EltName getSimpleName() {
		return simpleName;
	}

	@Override
	public ElementKind getKind() {
		return kind;
	}

	@Override
	public List<SrcElem> getEnclosedElements() {
		return enclosed;
	}

	@Override
	public abstract TpeMirror asType();

	@Override
	public List<? extends AnnotationMirror> getAnnotationMirrors() {
		return null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}

	@Override
	public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
		return null;
	}
}
