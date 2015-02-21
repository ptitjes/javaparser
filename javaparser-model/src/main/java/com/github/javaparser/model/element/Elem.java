package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public abstract class Elem implements Element {

	private final Origin origin;
	private final Scope parentScope;
	private final Elem enclosing;
	private final Set<Modifier> modifiers;
	private final EltSimpleName simpleName;
	private final ElementKind kind;
	private final List<Elem> enclosed = new ArrayList<Elem>();

	public Elem(Origin origin, Scope parentScope, Elem enclosing, Set<Modifier> modifiers, EltSimpleName simpleName, ElementKind kind) {
		this.origin = origin;
		this.parentScope = parentScope;
		this.enclosing = enclosing;
		this.modifiers = modifiers;
		this.simpleName = simpleName;
		this.kind = kind;

		if (this.enclosing != null) this.enclosing.addEnclosedElem(this);
	}

	public final Origin origin() {
		return origin;
	}

	public final Scope parentScope() {
		return parentScope;
	}

	public abstract Scope scope();

	protected void addEnclosedElem(Elem elem) {
		enclosed.add(elem);
	}

	@Override
	public final Elem getEnclosingElement() {
		return enclosing;
	}

	@Override
	public final Set<Modifier> getModifiers() {
		return modifiers;
	}

	@Override
	public final EltSimpleName getSimpleName() {
		return simpleName;
	}

	@Override
	public final ElementKind getKind() {
		return kind;
	}

	@Override
	public final List<Elem> getEnclosedElements() {
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
