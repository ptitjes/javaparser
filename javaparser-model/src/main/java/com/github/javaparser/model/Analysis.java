package com.github.javaparser.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.phases.Scaffolding;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.scope.RootScope;
import com.github.javaparser.model.scope.Scope;

import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class Analysis {

	private Scaffolding scaffolding = new Scaffolding(this);

	private final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
	private final Map<EltName, List<PackageElem>> dependencyPackages = new HashMap<EltName, List<PackageElem>>();
	private final Map<EltName, PackageElem> sourcePackages = new HashMap<EltName, PackageElem>();

	public void addCompilationUnit(CompilationUnit cu) {
		compilationUnits.add(cu);
		scaffolding.process(cu);
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

	public List<? extends PackageElement> getElements() {
		return null;
	}

	public Types getTypeUtils() {
		return null;
	}

	public Elements getElementUtils() {
		return null;
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
			return Collections.singletonList(sourcePackages.get(name));
		}
	};
}
