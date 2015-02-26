package com.github.javaparser.model.classpath;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

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
	public Set<ClasspathElement> getElements(String extension) {
		FileExtensionFilter filter = new FileExtensionFilter(extension);

		Set<ClasspathElement> elements = new HashSet<ClasspathElement>();
		for (File child : directory.listFiles(filter)) {
			String path = basePath.isEmpty() ? child.getName() : basePath + "/" + child.getName();
			if (child.isFile()) {
				elements.add(new FileClasspathElement(path, child));
			}
		}
		return elements;
	}

	public static Set<ClasspathElement> getAllElements(List<ClasspathSource> sources, String extension) {
		Queue<ClasspathSource> directories = new ArrayDeque<ClasspathSource>();
		List<ClasspathElement> sourceFiles = new ArrayList<ClasspathElement>();

		directories.addAll(sources);
		while (!directories.isEmpty()) {
			ClasspathSource current = directories.poll();

			directories.addAll(current.getSubtrees());
			sourceFiles.addAll(current.getElements(extension));
		}
		return new HashSet<ClasspathElement>(sourceFiles);
	}

	private static class FileExtensionFilter implements FileFilter {
		private String extension;

		public FileExtensionFilter(String extension) {
			this.extension = extension;
		}

		@Override
		public boolean accept(File pathname) {
			return pathname.getName().endsWith(extension);
		}
	}
}
