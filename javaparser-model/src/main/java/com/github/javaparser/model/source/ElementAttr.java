/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2015 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaParser.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.javaparser.model.source;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.VariableElem;
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

	public static ElementAttr<ExecutableElem> get(AnnotationMemberDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<ExecutableElem> get(InitializerDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<VariableElem> get(FieldDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<VariableElem> get(EnumConstantDeclaration n) {
		return doGet(n);
	}

	public static ElementAttr<VariableElem> get(Parameter n) {
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
