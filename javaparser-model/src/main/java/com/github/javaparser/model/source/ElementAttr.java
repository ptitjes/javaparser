package com.github.javaparser.model.source;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.scope.Scope;

/**
 * @author Didier Villevalois
 */
public class ElementAttr<E extends Elem> extends Attributes {

	public static ElementAttr<TypeElem> get(ClassOrInterfaceDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<TypeElem> get(AnnotationDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<TypeElem> get(EnumDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<TypeElem> get(ObjectCreationExpr n) {
		return doGet(n);
	}

	public static ElementAttr<ExecutableElem> get(ConstructorDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<ExecutableElem> get(MethodDeclaration n) {
		return doGet(n);
	}

	@SuppressWarnings("unchecked")
	private static <E extends Elem> ElementAttr<E> doGet(Node n) {
		return (ElementAttr<E>) Attributes.get(n);
	}

	private final E elem;

	public ElementAttr(CompilationUnit source, Node node, E elem) {
		super(source, node);
		this.elem = elem;
	}

	@Override
	public E element() {
		return elem;
	}

	@Override
	public Scope scope() {
		return elem.scope();
	}
}
