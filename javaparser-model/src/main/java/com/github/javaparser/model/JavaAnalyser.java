package com.github.javaparser.model;

import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ClasspathSource;
import com.github.javaparser.model.classpath.DirectorySource;
import com.github.javaparser.model.element.ElementUtils;
import com.github.javaparser.model.phases.Scaffolding;
import com.github.javaparser.model.phases.SurfaceTyping1;
import com.github.javaparser.model.phases.SurfaceTyping2;
import com.github.javaparser.model.phases.TypeResolver;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.type.TypeUtils;

import java.io.File;
import java.io.IOException;

/**
 * @author Didier Villevalois
 */
public class JavaAnalyser {

	private final AnalysisConfiguration configuration;

	public JavaAnalyser() {
		this(new AnalysisConfiguration());
	}

	public JavaAnalyser(AnalysisConfiguration configuration) {
		this.configuration = configuration;
	}

	public Analysis buildModel(File sourceDirectory) {
		return buildModel(new DirectorySource(sourceDirectory));
	}

	public Analysis buildModel(ClasspathSource sourceDirectory) {
		Classpath classpath = new Classpath();
		classpath.addSourceFiles(sourceDirectory);

		return buildModel(classpath);
	}

	public Analysis buildModel(final Classpath classpath) {
		Registry registry = new Registry();

		registry.register(classpath);
		registry.register(Reporter.class, configuration.getReporter());
		registry.register(new Scaffolding());
		registry.register(new TypeResolver());
		registry.register(new SurfaceTyping1());
		registry.register(new SurfaceTyping2());
		registry.register(new TypeUtils());
		registry.register(new ElementUtils());
		registry.configure();

		Analysis analysis = new Analysis(configuration, registry);
		analysis.proceed();
		return analysis;
	}
}
