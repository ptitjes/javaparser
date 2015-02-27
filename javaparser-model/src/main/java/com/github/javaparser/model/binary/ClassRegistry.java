package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.element.ElemDereferenceException;
import com.github.javaparser.model.element.ElemRef;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.scope.EltName;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author Federico Tomassetti
 */
public class ClassRegistry implements Registry.Participant {

	private ClassFileReader classFileReader;
	private Classpath classpath;

	private Map<EltName, ClasspathElement> packages = new HashMap<EltName, ClasspathElement>();
	private Map<EltName, ClasspathElement> classes = new HashMap<EltName, ClasspathElement>();
	private Map<EltName, ElemRef<TypeElem>> requestedClasses = new HashMap<EltName, ElemRef<TypeElem>>();

	@Override
	public void configure(Registry registry) {
		classFileReader = registry.get(ClassFileReader.class);
		classpath = registry.get(Classpath.class);
	}

	public void indexClassFiles() throws IOException {
		Set<ClasspathElement> classFiles = new HashSet<ClasspathElement>();

		// Inspect all class file directories' content
		classFiles.addAll(Classpath.getElements(classpath.getClassFileSources(), ".class"));

		// Inspect all jars' content
		// ...


		// Index all packages and class names
		for (ClasspathElement classFile : classFiles) {
			String path = classFile.getPath();
			EltName logicalName = null; // ...
			if (path.endsWith("package.class")) {
				packages.put(logicalName, classFile);
			} else {
				classes.put(logicalName, classFile);
			}
		}
	}

	public ElemRef<TypeElem> getByName(String name) {
		return null;
	}

	// Logical to binary name and vice versa routines
	// ...

	private class BinTypeElemRef extends ElemRef.Lazy<TypeElem> {

		private ClasspathElement classpathElement;

		// Only for top-level classes
		public BinTypeElemRef(EltName qualifiedName, ClasspathElement classpathElement) {
			super(qualifiedName);
			this.classpathElement = classpathElement;
		}

		@Override
		public TypeElem load() {
			Elem enclosing = null;
			// if top-level
			//   get package ref and dereference it
			// else
			//   get outer class and dereference it

			try {
				return classFileReader.readClass(classpath.dependencyScope(),
						enclosing,
						classpathElement.getInputStream());
			} catch (IOException e) {
				throw new ElemDereferenceException("Can't load class '" + qualifiedName() + "'", e);
			}
		}
	}
}
