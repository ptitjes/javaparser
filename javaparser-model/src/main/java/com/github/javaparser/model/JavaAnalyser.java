package com.github.javaparser.model;

import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ClasspathSource;
import com.github.javaparser.model.classpath.DirSourcesFinder;
import com.github.javaparser.model.element.ElementUtils;
import com.github.javaparser.model.phases.Scaffolding;
import com.github.javaparser.model.phases.SurfaceTyping1;
import com.github.javaparser.model.phases.SurfaceTyping2;
import com.github.javaparser.model.phases.TypeResolver;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.type.TypeUtils;

import java.io.File;

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

	public Analysis buildModel(final File sourceDirectory) {
		return buildModel(new DirSourcesFinder(sourceDirectory));
	}

	public Analysis buildModel(final ClasspathSource sourceDirectory) {
		Registry registry = new Registry();

		Classpath classpath = new Classpath();

		registry.register(classpath);
		registry.register(Reporter.class, configuration.getReporter());
		registry.register(new Scaffolding());
		registry.register(new TypeResolver());
		registry.register(new SurfaceTyping1());
		registry.register(new SurfaceTyping2());
		registry.register(new TypeUtils());
		registry.register(new ElementUtils());
		registry.configure();

		classpath.addSources(sourceDirectory);

		Analysis analysis = new Analysis(configuration, registry);
		analysis.proceed();
		return analysis;
	}
}
