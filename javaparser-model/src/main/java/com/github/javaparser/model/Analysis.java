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

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.binary.ClassRegistry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.element.ElementUtils;
import com.github.javaparser.model.phases.Scaffolding;
import com.github.javaparser.model.phases.SurfaceTyping1;
import com.github.javaparser.model.phases.SurfaceTyping2;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.type.TypeUtils;

import javax.lang.model.element.PackageElement;
import java.io.IOException;
import java.util.*;

/**
 * The result of a java source code analysis.
 *
 * @author Didier Villevalois
 */
public class Analysis {

	private final AnalysisConfiguration configuration;

	private final Reporter reporter;
	private final Classpath classpath;
	private final ClassRegistry classRegistry;

	private final Scaffolding scaffolding;
	private final SurfaceTyping1 surfaceTyping1;
	private final SurfaceTyping2 surfaceTyping2;

	private final TypeUtils typeUtils;
	private final ElementUtils elementUtils;

	Analysis(Registry registry) {
		configuration = registry.get(AnalysisConfiguration.class);
		reporter = registry.get(Reporter.class);

		classpath = registry.get(Classpath.class);
		classRegistry = registry.get(ClassRegistry.class);

		scaffolding = registry.get(Scaffolding.class);
		surfaceTyping1 = registry.get(SurfaceTyping1.class);
		surfaceTyping2 = registry.get(SurfaceTyping2.class);

		typeUtils = registry.get(TypeUtils.class);
		elementUtils = registry.get(ElementUtils.class);
	}

	void proceed() {
		Set<ClasspathElement> sourceFiles;
		try {
			sourceFiles = Classpath.getElements(classpath.getSourceFileSources(), ".java");
		} catch (IOException e) {
			reporter.report("Can't retrieve source files", e);
			return;
		}

		try {
			classRegistry.indexClassFiles();
		} catch (IOException e) {
			reporter.report("Can't index class files", e);
			return;
		}

		for (ClasspathElement sourceFile : sourceFiles) {
			try {
				CompilationUnit cu = JavaParser.parse(sourceFile.getInputStream(),
						configuration.encoding(),
						configuration.considerComments());
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

	public boolean hasErrors() {
		return reporter.hasErrors();
	}

	public List<? extends PackageElement> getSourcePackages() {
		return classpath.getSourcePackages();
	}

	public List<CompilationUnit> getCompilationUnits() {
		return classpath.getCompilationUnits();
	}
}
