package com.github.javaparser.model.classpath;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

/**
 * Retrieves ClasspathElement from a directory.
 *
 * @author Federico Tomassetti
 */
public class DirSourcesFinder implements ClasspathSource {
	private File directory;
	private String basePath;

	public DirSourcesFinder(File directory) {
		this(directory, "");
	}

	private DirSourcesFinder(File directory, String basePath) {
		if (!directory.exists()) {
			throw new IllegalArgumentException("No such directory: " + directory.getAbsolutePath());
		}
		this.directory = directory;
		this.basePath = basePath;
	}

	@Override
	public Set<ClasspathSource> getSubtrees() {
		Set<ClasspathSource> subtrees = new HashSet<ClasspathSource>();
		for (File child : directory.listFiles()) {
			String path = basePath.isEmpty() ? child.getName() : basePath + "/" + child.getName();
			if (child.isDirectory()) {
				subtrees.add(new DirSourcesFinder(child, path));
			}
		}
		return subtrees;
	}

	@Override
	public Set<ClasspathElement> getElements() {
		Set<ClasspathElement> elements = new HashSet<ClasspathElement>();
		for (File child : directory.listFiles()) {
			String path = basePath.isEmpty() ? child.getName() : basePath + "/" + child.getName();
			if (child.isFile()) {
				elements.add(new FileClasspathElement(path, child));
			}
		}
		return elements;
	}
}
