package com.github.javaparser.model.classpath;

import java.io.File;
import java.io.FileFilter;
import java.util.*;

/**
 * Retrieves ClasspathElement from a directory.
 *
 * @author Federico Tomassetti
 */
public class DirectorySource implements ClasspathSource {
	private final File directory;
	private final String basePath;

	public DirectorySource(File directory) {
		this(directory, "");
	}

	private DirectorySource(File directory, String basePath) {
		if (!directory.exists()) {
			throw new IllegalArgumentException("No such directory: " + directory.getAbsolutePath());
		}
		this.directory = directory;
		this.basePath = basePath;
	}

	private static FileFilter directoryFilter = new FileFilter() {
		@Override
		public boolean accept(File pathname) {
			return pathname.isDirectory();
		}
	};

	@Override
	public Set<ClasspathElement> getElements(String extension) {
		FileExtensionFilter filter = new FileExtensionFilter(extension);

		Queue<File> directories = new ArrayDeque<File>();
		directories.add(directory);

		Set<ClasspathElement> elements = new HashSet<ClasspathElement>();
		while (!directories.isEmpty()) {
			File currentDirectory = directories.poll();
			directories.addAll(Arrays.asList(currentDirectory.listFiles(directoryFilter)));

			for (File child : currentDirectory.listFiles(filter)) {
				String path = basePath.isEmpty() ? child.getName() : basePath + "/" + child.getName();
				elements.add(new FileClasspathElement(path, child));
			}
		}
		return elements;
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
