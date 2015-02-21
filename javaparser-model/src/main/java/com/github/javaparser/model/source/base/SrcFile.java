package com.github.javaparser.model.source.base;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.source.Attributes;
import com.github.javaparser.model.source.element.SrcPackageElem;

import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;

import static com.github.javaparser.model.source.utils.SrcNameUtils.asName;

/**
 * @author Didier Villevalois
 */
public class SrcFile extends Attributes {

	private final Analysis analysis;
	private final SrcPackageElem packageElem;

	public SrcFile(Analysis analysis, SrcPackageElem packageElem, CompilationUnit node) {
		super(null, node);
		this.analysis = analysis;
		this.packageElem = packageElem;
	}

	public Analysis analysis() {
		return analysis;
	}

	public SrcPackageElem packageElem() {
		return packageElem;
	}

	public SrcFile source() {
		return this;
	}

	@Override
	public CompilationUnit node() {
		return (CompilationUnit) super.node();
	}

	public Scope definedScope() {
		return scope;
	}

	private final Scope scope = new Scope(null) {

		@Override
		public TypeElement resolveLocalType(EltName name) {
			for (ImportDeclaration importDecl : node().getImports()) {
				EltName importName = asName(importDecl.getName());
				if (importName.getSimpleName().equals(name.getRootQualifier())) {
					TypeElement elem = rootScope().resolveType(importName);
					// TODO dig in elem for (name minus root qualifier)
					return elem;
				}
			}

			return null;
		}

		@Override
		public VariableElement resolveLocalVariable(EltName name) {
			return null;
		}

		@Override
		public ExecutableElement resolveLocalExecutable(EltName name) {
			return null;
		}
	};
}
