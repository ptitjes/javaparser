package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.source.ElementAttr;
import com.github.javaparser.model.type.TypeUtils;

/**
 * @author Didier Villevalois
 */
public class SurfaceTyping2 implements Registry.Participant {

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
		public void visit(MethodDeclaration n, Void arg) {
			ElementAttr<ExecutableElem> attr = ElementAttr.get(n);
			ExecutableElem elem = attr.element();

			super.visit(n, arg);
		}
	};
}
