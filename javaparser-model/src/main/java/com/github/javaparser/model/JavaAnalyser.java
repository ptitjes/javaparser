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

package com.github.javaparser.model;

import com.github.javaparser.model.binary.BinaryTypeBuilder;
import com.github.javaparser.model.binary.ClassFileReader;
import com.github.javaparser.model.binary.ClassRegistry;
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

/**
 * The main entry point to JavaParser Model.
 *
 * @author Didier Villevalois
 */
public class JavaAnalyser {

	private final AnalysisConfiguration configuration;

	/**
	 * Creates a java analyser with default configuration.
	 */
	public JavaAnalyser() {
		this(new AnalysisConfiguration());
	}

	/**
	 * Creates a java analyser with the specified configuration.
	 *
	 * @param configuration the configuration for the java analyser
	 */
	public JavaAnalyser(AnalysisConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 * Analyse the source files in the specified directory.
	 *
	 * @param sourceDirectory the source directory
	 */
	public Analysis analyse(File sourceDirectory) {
		return analyse(new DirectorySource(sourceDirectory));
	}

	/**
	 * Analyse the source files in the specified classpath source.
	 *
	 * @param sources the source classpath source
	 */
	public Analysis analyse(ClasspathSource sources) {
		Classpath classpath = new Classpath();
		classpath.addSourceFiles(sources);

		return analyse(classpath);
	}

	/**
	 * Analyse the source files described by the specified classpath object.
	 *
	 * @param classpath the classpath descriptor object
	 */
	public Analysis analyse(final Classpath classpath) {
		Registry registry = new Registry();

		registry.register(configuration);
		registry.register(classpath);
		registry.register(Reporter.class, configuration.reporter());
		registry.register(new ClassRegistry());
		registry.register(new ClassFileReader());
		registry.register(new BinaryTypeBuilder());
		registry.register(new Scaffolding());
		registry.register(new TypeResolver());
		registry.register(new SurfaceTyping1());
		registry.register(new SurfaceTyping2());
		registry.register(new TypeUtils());
		registry.register(new ElementUtils());
		registry.configure();

		Analysis analysis = new Analysis(registry);
		analysis.proceed();
		return analysis;
	}
}
