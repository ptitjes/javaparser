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

package com.github.javaparser.model.classpath;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.Registry;
import com.github.javaparser.model.binary.ClassRegistry;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.RootScope;
import com.github.javaparser.model.scope.Scope;

import javax.lang.model.element.PackageElement;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class Classpath implements Registry.Participant {

	private final List<ClasspathSource> sourceFileSources = new ArrayList<ClasspathSource>();
	private final List<ClasspathSource> classFileSources = new ArrayList<ClasspathSource>();

	private final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();

	private final Map<EltName, List<PackageElem>> dependencyPackages = new HashMap<EltName, List<PackageElem>>();
	private final Map<EltName, PackageElem> sourcePackages = new HashMap<EltName, PackageElem>();

	private ClassRegistry classRegistry;

	public Classpath() {
	}

	@Override
	public void configure(Registry registry) {
		// TODO Classpath should be a participant
		// Create a participant to handle the source scope
		classRegistry = registry.get(ClassRegistry.class);
	}

	public void addSourceFiles(final ClasspathSource classpathSource) {
		sourceFileSources.add(classpathSource);
	}

	public void addClassFiles(final ClasspathSource classpathSource) {
		classFileSources.add(classpathSource);
	}

	public void addSourceFileDirectory(final File directory) {
		addSourceFiles(new DirectorySource(directory));
	}

	public void addClassFileDirectory(final File directory) {
		addClassFiles(new DirectorySource(directory));
	}

	public void addJars(final File directory) {
		if (!directory.exists())
			throw new IllegalArgumentException("No such directory: " + directory.getAbsolutePath());

		for (File element : directory.listFiles(jarFileFilter)) {
			addJar(element);
		}
	}

	public void addJar(final File jarFile) {
		addClassFiles(new JarFileSource(jarFile));
	}

	public List<ClasspathSource> getSourceFileSources() {
		return sourceFileSources;
	}

	public List<ClasspathSource> getClassFileSources() {
		return classFileSources;
	}

	public static Set<ClasspathElement> getElements(List<ClasspathSource> sources, String extension) throws IOException {
		Set<ClasspathElement> elements = new HashSet<ClasspathElement>();
		for (ClasspathSource source : sources) {
			elements.addAll(source.getElements(extension));
		}
		return elements;
	}

	public void addCompilationUnit(CompilationUnit cu) {
		compilationUnits.add(cu);
	}

	public List<CompilationUnit> getCompilationUnits() {
		return compilationUnits;
	}

	public List<PackageElem> getDependencyPackages(EltName name) {
		List<PackageElem> packageElems = dependencyPackages.get(name);
		if (packageElems == null) return Collections.emptyList();
		return packageElems;
	}

	public void addDependencyPackage(PackageElem packageElem) {
		EltName name = packageElem.getQualifiedName();
		List<PackageElem> packageElems = dependencyPackages.get(name);
		if (packageElems == null) {
			packageElems = new ArrayList<PackageElem>();
			dependencyPackages.put(name, packageElems);
		}
		packageElems.add(packageElem);
	}

	public List<? extends PackageElement> getDependencyPackages() {
		ArrayList<PackageElement> allPackageElements = new ArrayList<PackageElement>();
		for (List<PackageElem> packageElements : dependencyPackages.values()) {
			allPackageElements.addAll(packageElements);
		}
		return allPackageElements;
	}

	public PackageElem getSourcePackage(EltName name) {
		return sourcePackages.get(name);
	}

	public void addSourcePackage(PackageElem packageElem) {
		EltName name = packageElem.getQualifiedName();
		if (sourcePackages.containsKey(name))
			throw new IllegalStateException("Package already defined");
		sourcePackages.put(name, packageElem);
	}

	public List<? extends PackageElement> getSourcePackages() {
		return new ArrayList<PackageElement>(sourcePackages.values());
	}

	public Scope sourceScope() {
		return sourceScope;
	}

	private RootScope sourceScope = new RootScope() {
		@Override
		public Scope parentScope() {
			return classRegistry.dependencyScope();
		}

		@Override
		public List<PackageElem> resolvePackages(EltName name) {
			PackageElem packageElem = sourcePackages.get(name);
			if (packageElem == null) return classRegistry.dependencyScope().resolvePackages(name);
			else return Collections.singletonList(packageElem);
		}
	};

	private static FileFilter jarFileFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(".jar");
		}
	};
}
