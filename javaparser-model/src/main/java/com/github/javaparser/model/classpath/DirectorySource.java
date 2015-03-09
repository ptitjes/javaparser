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
