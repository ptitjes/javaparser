package com.github.javaparser.model.classpath;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.element.Origin;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltNames;
import com.github.javaparser.model.scope.RootScope;
import com.github.javaparser.model.scope.Scope;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import javax.lang.model.element.PackageElement;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class Classpath {

	private final List<ClasspathSource> sourceFileSources = new ArrayList<ClasspathSource>();
	private final List<ClasspathSource> classFileSources = new ArrayList<ClasspathSource>();

	private final List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();

	private final Map<EltName, List<PackageElem>> dependencyPackages = new HashMap<EltName, List<PackageElem>>();
	private final Map<EltName, PackageElem> sourcePackages = new HashMap<EltName, PackageElem>();

	public Classpath() {
		addTemporaryFakeJavaLangPackage();
	}

	private void addTemporaryFakeJavaLangPackage() {
		Origin fakeBinaryOrigin = new Origin() {
			@Override
			public String toLocationString() {
				return "Fake";
			}
		};

		PackageElem packageElem = new PackageElem(dependencyScope, fakeBinaryOrigin, EltNames.make("java.lang"));
		new TypeElem(fakeBinaryOrigin, packageElem.scope(), packageElem, EnumSet.of(Modifier.PUBLIC),
				EltNames.make("java.lang.Object"), EltNames.makeSimple("Object"), ElementKind.CLASS, NestingKind.TOP_LEVEL);
		new TypeElem(fakeBinaryOrigin, packageElem.scope(), packageElem, EnumSet.of(Modifier.PUBLIC),
				EltNames.make("java.lang.Enum"), EltNames.makeSimple("Enum"), ElementKind.CLASS, NestingKind.TOP_LEVEL);

		addDependencyPackage(packageElem);
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

	public Scope dependencyScope() {
		return dependencyScope;
	}

	private RootScope dependencyScope = new RootScope() {
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

	private RootScope sourceScope = new RootScope() {
		@Override
		public Scope parentScope() {
			return dependencyScope;
		}

		@Override
		public List<PackageElem> resolvePackages(EltName name) {
			PackageElem packageElem = sourcePackages.get(name);
			if (packageElem == null) return dependencyScope.resolvePackages(name);
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
