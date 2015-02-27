package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.element.*;
import com.github.javaparser.model.scope.*;

import java.io.IOException;
import java.util.*;

/**
 * @author Federico Tomassetti
 */
public class ClassRegistry implements Registry.Participant {

	public static final String CLASS_EXT = ".class";
	public static final int CLASS_EXT_LENGTH = CLASS_EXT.length();
	private ClassFileReader classFileReader;
	private Classpath classpath;

	private Map<EltName, ClasspathElement> packages = new HashMap<EltName, ClasspathElement>();
	private Map<EltName, ClasspathElement> classes = new HashMap<EltName, ClasspathElement>();
	private Map<EltName, ElemRef<TypeElem>> requestedClasses = new HashMap<EltName, ElemRef<TypeElem>>();
	private Map<EltName, PackageElem> loadedPackages = new HashMap<EltName, PackageElem>();
	private Map<EltName, TypeElem> loadedClasses = new HashMap<EltName, TypeElem>();

	private Map<String, EltName> internalToLogical = new HashMap<String, EltName>();
	private Map<EltName, String> logicalToInternal = new HashMap<EltName, String>();

	@Override
	public void configure(Registry registry) {
		classFileReader = registry.get(ClassFileReader.class);
		classpath = registry.get(Classpath.class);
	}

	public void indexClassFiles() throws IOException {
		Set<ClasspathElement> classFiles = new HashSet<ClasspathElement>();

		// Inspect all class files
		classFiles.addAll(Classpath.getElements(classpath.getClassFileSources(), CLASS_EXT));

		// Index all packages and class names
		for (ClasspathElement classFile : classFiles) {
			String path = classFile.getPath();
			String packagePath = path.substring(0, path.lastIndexOf('/'));

			EltName logicalName = toLogicalName(path.substring(0, path.length() - CLASS_EXT_LENGTH));
			if (path.endsWith("package.class")) {
				packages.put(logicalName, classFile);
			} else {
				classes.put(logicalName, classFile);
			}
		}
	}

	// Logical to binary name and vice versa routines

	private EltName toLogicalName(String internalName) {
		EltName logicalName = internalToLogical.get(internalName);
		if (logicalName != null) {
			return logicalName;
		}

		logicalName = EltNames.make(internalName.replace('/', '.').replace('$', '.'));
		internalToLogical.put(internalName, logicalName);
		logicalToInternal.put(logicalName, internalName);
		return logicalName;
	}

	private String toInternalName(EltName logical) {
		return logicalToInternal.get(logical);
	}

	public ElemRef<TypeElem> typeRef(String internalName) {
		EltName name = toLogicalName(internalName);
		if (loadedClasses.containsKey(name)) {
			return loadedClasses.get(name).asRef();
		}
		if (requestedClasses.containsKey(name)) {
			return requestedClasses.get(name);
		}

		BinTypeElemRef ref = new BinTypeElemRef(name);
		requestedClasses.put(name, ref);
		return ref;
	}

	private PackageElem loadPackage(EltName qualifiedName) throws IOException {
		if (loadedPackages.containsKey(qualifiedName)) {
			return loadedPackages.get(qualifiedName);
		}

		PackageElem elem;
		ClasspathElement classpathElement = packages.get(qualifiedName);
		if (classpathElement != null) {
			elem = new PackageElem(dependencyScope, new BinaryOrigin(classpathElement.getPath()), qualifiedName);
			// TODO Implement reading package.class files
			// elem = classFileReader.readPackage(dependencyScope(), classpathElement.getInputStream());
		} else {
			// No package.class file
			elem = new PackageElem(dependencyScope, new BinaryOrigin(toInternalName(qualifiedName)), qualifiedName);
		}
		loadedPackages.put(qualifiedName, elem);
		return elem;
	}

	private TypeElem loadClass(EltName qualifiedName) throws IOException {
		if (loadedClasses.containsKey(qualifiedName)) {
			return loadedClasses.get(qualifiedName);
		}

		Elem enclosing = null;

		String internalName = toInternalName(qualifiedName);
		int dollarIndex = internalName.lastIndexOf('$');
		if (dollarIndex != -1) {
			String enclosingInternalName = internalName.substring(0, dollarIndex);
			enclosing = loadClass(toLogicalName(enclosingInternalName));
		} else {
			// Top-level class
			enclosing = loadPackage(qualifiedName.qualifier());
		}

		ClasspathElement classpathElement = classes.get(qualifiedName);
		TypeElem elem = classFileReader.readClass(dependencyScope(), enclosing, qualifiedName,
				classpathElement.getInputStream());
		loadedClasses.put(qualifiedName, elem);
		return elem;
	}

	private class BinTypeElemRef extends ElemRef.Lazy<TypeElem> {

		// Only for top-level classes
		public BinTypeElemRef(EltName qualifiedName) {
			super(qualifiedName);
		}

		@Override
		public TypeElem load() {
			try {
				return loadClass(qualifiedName());
			} catch (IOException e) {
				throw new ElemDereferenceException("Can't load class '" + qualifiedName() + "'", e);
			}
		}
	}

	public RootScope dependencyScope() {
		return dependencyScope;
	}

	private RootScope dependencyScope = new RootScope() {
		@Override
		public Scope parentScope() {
			return null;
		}

		@Override
		public List<PackageElem> resolvePackages(EltName name) {
			return null; //dependencyPackages.get(name);
		}

		@Override
		public TypeElem resolveType(EltName name) {
			if (!classes.containsKey(name)) return null;

			try {
				return loadClass(name);
			} catch (IOException e) {
				throw new ScopeException("Error loading class " + name, e);
			}
		}
	};
}
