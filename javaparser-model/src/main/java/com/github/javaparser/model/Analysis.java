package com.github.javaparser.model;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.phases.Scaffolding;
import com.github.javaparser.model.source.element.SrcPackageElem;

import javax.lang.model.element.PackageElement;
import javax.lang.model.util.Elements;
import javax.lang.model.util.Types;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public class Analysis {

	private final List<CompilationUnit> compilationUnits;
	private final Map<EltName, SrcPackageElem> sourcePackages = new HashMap<EltName, SrcPackageElem>();

	// TODO Add external libraries
	public Analysis(List<CompilationUnit> compilationUnits) {
		this.compilationUnits = compilationUnits;
	}

	public void run() {
		new Scaffolding().scaffold(this);
	}

	public List<CompilationUnit> getCompilationUnits() {
		return compilationUnits;
	}

	public SrcPackageElem getSourcePackage(EltName name) {
		SrcPackageElem packageElem = sourcePackages.get(name);
		if (packageElem == null) {
			packageElem = new SrcPackageElem(name);
			sourcePackages.put(name, packageElem);
		}
		return packageElem;
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
}
