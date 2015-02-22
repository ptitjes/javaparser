package com.github.javaparser.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.element.ElementUtils;
import com.github.javaparser.model.element.Origin;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.phases.Scaffolding;
import com.github.javaparser.model.phases.SuperTypeResolution;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.RootScope;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.TypeUtils;

import javax.lang.model.element.PackageElement;
import java.io.File;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class Analysis implements Reporter {

	private final AnalysisConfiguration configuration;
	private final TypeUtils typeUtils = new TypeUtils(this);
	private final ElementUtils elementUtils = new ElementUtils(this);

	private boolean errors = false;

	private final Scaffolding scaffolding = new Scaffolding(this);
	private final SuperTypeResolution superTypeResolution = new SuperTypeResolution(this);

	private final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
	private final Map<EltName, List<PackageElem>> dependencyPackages = new HashMap<EltName, List<PackageElem>>();
	private final Map<EltName, PackageElem> sourcePackages = new HashMap<EltName, PackageElem>();

	public Analysis(AnalysisConfiguration configuration) {
		this.configuration = configuration;
	}

	public void addCompilationUnit(File file, CompilationUnit cu) {
		compilationUnits.add(cu);
		scaffolding.process(file, cu);
		superTypeResolution.process();
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

	public PackageElem getSourcePackage(EltName name) {
		return sourcePackages.get(name);
	}

	public void addSourcePackage(PackageElem packageElem) {
		EltName name = packageElem.getQualifiedName();
		if (sourcePackages.containsKey(name))
			throw new IllegalStateException("Package already defined");
		sourcePackages.put(name, packageElem);
	}

	public List<? extends PackageElement> getSourcePackageElements() {
		return new ArrayList<PackageElement>(sourcePackages.values());
	}

	public TypeUtils getTypeUtils() {
		return typeUtils;
	}

	public ElementUtils getElementUtils() {
		return elementUtils;
	}

	public Scope dependencyScope() {
		return dependencyScope;
	}

	private Scope dependencyScope = new RootScope() {
		@Override
		public Scope parentScope() {
			return null;
		}

		@Override
		public List<PackageElem> resolvePackages(EltName name) {
			// TODO Should trigger lazy loading ??
			return dependencyPackages.get(name);
		}
	};

	public Scope sourceScope() {
		return sourceScope;
	}

	private Scope sourceScope = new RootScope() {
		@Override
		public Scope parentScope() {
			return dependencyScope;
		}

		@Override
		public List<PackageElem> resolvePackages(EltName name) {
			PackageElem packageElem = sourcePackages.get(name);
			if (packageElem == null) return null;
			return Collections.singletonList(packageElem);
		}
	};

	@Override
	public void report(File file, Exception exception) {
		errors = true;
		configuration.getReporter().report(file, exception);
	}

	@Override
	public void report(Severity severity, String message, Origin origin) {
		if (severity == Severity.ERROR) errors = true;
		configuration.getReporter().report(severity, message, origin);
	}

	public boolean hasErrors() {
		return errors;
	}
}
