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
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.VariableElem;
import com.github.javaparser.model.scope.*;

import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.model.source.utils.NodeListUtils.safeIterable;
import static com.github.javaparser.model.source.utils.SrcNameUtils.asName;

/**
 * @author Didier Villevalois
 */
public class CompilationUnitAttr extends Attributes {

	public static CompilationUnitAttr get(CompilationUnit n) {
		return (CompilationUnitAttr) Attributes.get(n);
	}

	private final Scope parentScope;
	private final ClasspathElement file;

	public CompilationUnitAttr(Scope parentScope, ClasspathElement file, CompilationUnit cu) {
		super(cu, cu);
		this.parentScope = parentScope;
		this.file = file;
	}

	public CompilationUnitAttr source() {
		return this;
	}

	public Scope parentScope() {
		return parentScope;
	}

	public ClasspathElement file() {
		return file;
	}

	@Override
	public CompilationUnit node() {
		return (CompilationUnit) super.node();
	}

	@Override
	public Scope scope() {
		return scope;
	}

	// TODO Refactor all this with generics and lambdas
	private final Scope scope = new Scope() {
		@Override
		public Scope parentScope() {
			return CompilationUnitAttr.this.parentScope();
		}

		@Override
		public TypeElem resolveLocalType(EltSimpleName name) {
			List<TypeElem> candidates = new ArrayList<TypeElem>();

			for (ImportDeclaration importDecl : safeIterable(node().getImports())) {
				if (importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());
				if (importName.simpleName().equals(name)) {
					TypeElem candidate = parentScope().resolveType(importName/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO Be more specific in reported message
				throw new ScopeException("Ambiguous imports for type '" + name + "'", node());
			}

			for (ImportDeclaration importDecl : safeIterable(node().getImports())) {
				if (!importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());

				if (importDecl.isStatic()) {
					TypeElem candidateParent = parentScope().resolveType(importName);
					if (candidateParent != null) {
						TypeElem candidate = candidateParent.scope().resolveLocalType(name/*, importDecl.isStatic()*/);
						if (candidate != null) candidates.add(candidate);
					}
				} else {
					TypeElem candidate = parentScope().resolveType(EltNames.make(importName, name));
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO Be more specific in reported message
				throw new ScopeException("Ambiguous imports for type '" + name + "'", node());
			}

			// Implicit java.lang.* import
			TypeElem candidate = parentScope().resolveType(EltNames.make(EltNames.make("java.lang"), name));
			if (candidate != null) return candidate;

			return null;
		}

		@Override
		public VariableElem resolveLocalVariable(EltSimpleName name) {
			List<VariableElem> candidates = new ArrayList<VariableElem>();

			for (ImportDeclaration importDecl : safeIterable(node().getImports())) {
				if (!importDecl.isStatic() || importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());
				if (importName.simpleName().equals(name)) {
					VariableElem candidate = parentScope().resolveVariable(importName/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO Be more specific in reported message
				throw new ScopeException("Ambiguous imports for variable '" + name + "'", node());
			}

			for (ImportDeclaration importDecl : safeIterable(node().getImports())) {
				if (!importDecl.isStatic() || !importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());

				TypeElem candidateParent = parentScope().resolveType(importName);
				if (candidateParent != null) {
					VariableElem candidate = candidateParent.scope().resolveLocalVariable(name/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO Be more specific in reported message
				throw new ScopeException("Ambiguous imports for variable '" + name + "'", node());
			}

			return null;
		}

		@Override
		public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
			List<ExecutableElem> candidates = new ArrayList<ExecutableElem>();

			for (ImportDeclaration importDecl : safeIterable(node().getImports())) {
				if (!importDecl.isStatic() || importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());
				if (importName.simpleName().equals(name)) {
					ExecutableElem candidate = parentScope().resolveExecutable(importName/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO Be more specific in reported message
				throw new ScopeException("Ambiguous imports for executable '" + name + "'", node());
			}

			for (ImportDeclaration importDecl : safeIterable(node().getImports())) {
				if (!importDecl.isStatic() || !importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());

				TypeElem candidateParent = parentScope().resolveType(importName);
				if (candidateParent != null) {
					ExecutableElem candidate = candidateParent.scope().resolveLocalExecutable(name/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO Be more specific in reported message
				throw new ScopeException("Ambiguous imports for executable '" + name + "'", node());
			}

			return null;
		}
	};
}
