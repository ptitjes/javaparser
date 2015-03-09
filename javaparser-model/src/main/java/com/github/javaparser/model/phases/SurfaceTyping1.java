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

package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.report.Reporter.Severity;
import com.github.javaparser.model.scope.ScopeException;
import com.github.javaparser.model.source.ElementAttr;
import com.github.javaparser.model.type.NoTpe;
import com.github.javaparser.model.type.TpeMirror;
import com.github.javaparser.model.type.TypeUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class SurfaceTyping1 implements Registry.Participant {

	private Reporter reporter;
	private Classpath classpath;
	private TypeUtils typeUtils;
	private TypeResolver typeResolver;

	@Override
	public void configure(Registry registry) {
		reporter = registry.get(Reporter.class);
		classpath = registry.get(Classpath.class);
		typeUtils = registry.get(TypeUtils.class);
		typeResolver = registry.get(TypeResolver.class);
	}

	public void process() {
		for (CompilationUnit cu : classpath.getCompilationUnits()) {
			cu.accept(scanner, null);
		}
	}

	private VoidVisitorAdapter<Void> scanner = new VoidVisitorAdapter<Void>() {

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			try {
				typeResolver.resolveTypeParameters(elem.getTypeParameters(), elem.scope());
			} catch (ScopeException ex) {
				reporter.report(Severity.ERROR, ex.getMessage(), elem.origin());
			}

			List<ClassOrInterfaceType> extended = n.getExtends();
			List<ClassOrInterfaceType> implemented = n.getImplements();

			if (n.isInterface()) {
				elem.setSuperClass(NoTpe.NONE);

				try {
					elem.setInterfaces(typeResolver.resolveTypes(extended, elem.scope()));
				} catch (ScopeException ex) {
					reportSuperTypesResolutionError(elem, extended, ex);
				}
			} else {
				if (extended != null && extended.size() == 1) {
					try {
						ClassOrInterfaceType type = extended.get(0);
						elem.setSuperClass(typeResolver.resolveType(type, elem.scope()));
					} catch (ScopeException ex) {
						reportSuperTypesResolutionError(elem, extended, ex);
					}
				} else elem.setSuperClass(NoTpe.NONE);

				try {
					elem.setInterfaces(typeResolver.resolveTypes(implemented, elem.scope()));
				} catch (ScopeException ex) {
					reportSuperTypesResolutionError(elem, implemented, ex);
				}
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(EnumDeclaration n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			List<ClassOrInterfaceType> implemented = n.getImplements();

			elem.setSuperClass(typeUtils.enumTypeOf(elem.asType()));

			try {
				elem.setInterfaces(typeResolver.resolveTypes(implemented, elem.scope()));
			} catch (ScopeException ex) {
				reportSuperTypesResolutionError(elem, implemented, ex);
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(AnnotationDeclaration n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			elem.setSuperClass(NoTpe.NONE);
			elem.setInterfaces(Collections.<TpeMirror>singletonList(typeUtils.annotationType()));

			super.visit(n, arg);
		}

		// Too early for now - may depends on enclosing executable's type parameters
		/*@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			ClassOrInterfaceType type = n.getType();

			try {
				DeclaredTpe tpeMirror = (DeclaredTpe) typeResolver.resolveType(type, elem.scope());
				switch (tpeMirror.asElement().getKind()) {
					case INTERFACE:
						elem.setSuperClass(typeUtils.objectType());
						elem.setInterfaces(Collections.<TpeMirror>singletonList(tpeMirror));
						break;
					case CLASS:
						elem.setSuperClass(tpeMirror);
						elem.setInterfaces(Collections.<TpeMirror>emptyList());
						break;
					default:
						throw new IllegalStateException();
				}
			} catch (ScopeException ex) {
				reporter.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), elem.origin());
			}

			super.visit(n, arg);
		}*/
	};

	private void reportSuperTypesResolutionError(TypeElem elem, List<ClassOrInterfaceType> implemented, ScopeException ex) {
		reporter.report(Severity.ERROR, "Can't resolve super-types '" + implemented + "' - " + ex.getMessage(), elem.origin());
	}
}
