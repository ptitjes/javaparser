package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.source.ElementAttr;
import com.github.javaparser.model.type.TypeUtils;

/**
 * @author Didier Villevalois
 */
public class SurfaceTyping2 {

	private final Analysis analysis;
	private final TypeUtils typeUtils;

	public SurfaceTyping2(Analysis analysis) {
		this.analysis = analysis;

		typeUtils = analysis.getTypeUtils();
	}

	public void process() {
		for (CompilationUnit cu : analysis.getCompilationUnits()) {
			cu.accept(scanner, null);
		}
	}

	private VoidVisitorAdapter<Void> scanner = new VoidVisitorAdapter<Void>() {

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			ElementAttr<ExecutableElem> attr = ElementAttr.get(n);
			ExecutableElem elem = attr.element();

			super.visit(n, arg);
		}
	};
}
