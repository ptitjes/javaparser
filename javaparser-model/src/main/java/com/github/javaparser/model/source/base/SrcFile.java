package com.github.javaparser.model.source.base;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.VariableElem;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.source.Attributes;

import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.model.source.utils.SrcNameUtils.asName;

/**
 * @author Didier Villevalois
 */
public class SrcFile extends Attributes {

	private final Scope parentScope;

	public SrcFile(Scope parentScope, CompilationUnit cu) {
		super(null, cu);
		this.parentScope = parentScope;
	}

	public SrcFile source() {
		return this;
	}

	public Scope parentScope() {
		return parentScope;
	}

	@Override
	public CompilationUnit node() {
		return (CompilationUnit) super.node();
	}

	public Scope scope() {
		return scope;
	}

	// TODO Refactor all this with generics and lambdas
	private final Scope scope = new Scope() {
		@Override
		public Scope parentScope() {
			return SrcFile.this.parentScope();
		}

		@Override
		public TypeElem resolveLocalType(EltSimpleName name) {
			List<TypeElem> candidates = new ArrayList<TypeElem>();

			for (ImportDeclaration importDecl : node().getImports()) {
				if (importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());
				if (importName.simpleName().equals(name)) {
					TypeElem candidate = rootScope().resolveType(importName/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO make custom runtime exception and handle gracefully in phases
				throw new IllegalStateException();
			}

			for (ImportDeclaration importDecl : node().getImports()) {
				if (!importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());

				TypeElem candidateParent = rootScope().resolveType(importName);
				if (candidateParent != null) {
					TypeElem candidate = candidateParent.scope().resolveLocalType(name/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO make custom runtime exception and handle gracefully in phases
				throw new IllegalStateException();
			}

			return null;
		}

		@Override
		public VariableElem resolveLocalVariable(EltSimpleName name) {
			List<VariableElem> candidates = new ArrayList<VariableElem>();

			for (ImportDeclaration importDecl : node().getImports()) {
				if (!importDecl.isStatic() || importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());
				if (importName.simpleName().equals(name)) {
					VariableElem candidate = rootScope().resolveVariable(importName/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO make custom runtime exception and handle gracefully in phases
				throw new IllegalStateException();
			}

			for (ImportDeclaration importDecl : node().getImports()) {
				if (!importDecl.isStatic() || !importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());

				TypeElem candidateParent = rootScope().resolveType(importName);
				if (candidateParent != null) {
					VariableElem candidate = candidateParent.scope().resolveLocalVariable(name/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO make custom runtime exception and handle gracefully in phases
				throw new IllegalStateException();
			}

			return null;
		}

		@Override
		public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
			List<ExecutableElem> candidates = new ArrayList<ExecutableElem>();

			for (ImportDeclaration importDecl : node().getImports()) {
				if (!importDecl.isStatic() || importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());
				if (importName.simpleName().equals(name)) {
					ExecutableElem candidate = rootScope().resolveExecutable(importName/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO make custom runtime exception and handle gracefully in phases
				throw new IllegalStateException();
			}

			for (ImportDeclaration importDecl : node().getImports()) {
				if (!importDecl.isStatic() || !importDecl.isAsterisk()) continue;

				EltName importName = asName(importDecl.getName());

				TypeElem candidateParent = rootScope().resolveType(importName);
				if (candidateParent != null) {
					ExecutableElem candidate = candidateParent.scope().resolveLocalExecutable(name/*, importDecl.isStatic()*/);
					if (candidate != null) candidates.add(candidate);
				}
			}
			if (candidates.size() == 1) {
				return candidates.get(0);
			} else if (candidates.size() > 1) {
				// TODO make custom runtime exception and handle gracefully in phases
				throw new IllegalStateException();
			}

			return null;
		}
	};
}
