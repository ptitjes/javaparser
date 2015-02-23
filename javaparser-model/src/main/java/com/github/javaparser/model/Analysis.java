package com.github.javaparser.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.classpath.ClasspathSource;
import com.github.javaparser.model.element.ElementUtils;
import com.github.javaparser.model.phases.Scaffolding;
import com.github.javaparser.model.phases.SurfaceTyping1;
import com.github.javaparser.model.phases.SurfaceTyping2;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.type.TypeUtils;

import javax.lang.model.element.PackageElement;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

/**
 * @author Didier Villevalois
 */
public class Analysis {

	private final AnalysisConfiguration configuration;

	private final Reporter reporter;
	private final Classpath classpath;
	private final Scaffolding scaffolding;
	private final SurfaceTyping1 surfaceTyping1;
	private final SurfaceTyping2 surfaceTyping2;

	private final TypeUtils typeUtils;
	private final ElementUtils elementUtils;

	public Analysis(AnalysisConfiguration configuration, Registry registry) {
		this.configuration = configuration;

		reporter = registry.get(Reporter.class);
		classpath = registry.get(Classpath.class);
		scaffolding = registry.get(Scaffolding.class);
		surfaceTyping1 = registry.get(SurfaceTyping1.class);
		surfaceTyping2 = registry.get(SurfaceTyping2.class);

		typeUtils = registry.get(TypeUtils.class);
		elementUtils = registry.get(ElementUtils.class);
	}

	public void proceed() {
		Queue<ClasspathSource> directories = new ArrayDeque<ClasspathSource>();
		List<ClasspathElement> sourceFiles = new ArrayList<ClasspathElement>();

		directories.addAll(classpath.getSources());
		while (!directories.isEmpty()) {
			ClasspathSource current = directories.poll();

			directories.addAll(current.getSubtrees());
			sourceFiles.addAll(current.getElements());
		}

		for (ClasspathElement sourceFile : sourceFiles) {
			try {
				CompilationUnit cu = JavaParser.parse(sourceFile.getInputStream(),
						configuration.getEncoding(),
						configuration.isConsideringComments());
				classpath.addCompilationUnit(cu);
				scaffolding.process(sourceFile, cu);
			} catch (ParseException e) {
				reporter.report(sourceFile, e);
			} catch (IOException e) {
				reporter.report(sourceFile, e);
			}
		}

		surfaceTyping1.process();
		surfaceTyping2.process();
	}

	public TypeUtils getTypeUtils() {
		return typeUtils;
	}

	public ElementUtils getElementUtils() {
		return elementUtils;
	}

	boolean hasErrors() {
		return reporter.hasErrors();
	}

	public List<? extends PackageElement> getSourcePackages() {
		return classpath.getSourcePackages();
	}

	public List<CompilationUnit> getCompilationUnits() {
		return classpath.getCompilationUnits();
	}
}
