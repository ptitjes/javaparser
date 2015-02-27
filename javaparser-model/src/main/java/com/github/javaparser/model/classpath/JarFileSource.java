package com.github.javaparser.model.classpath;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URLDecoder;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Retrieves ClasspathElement from a jar file.
 *
 * @author Federico Tomassetti
 */
public class JarFileSource implements ClasspathSource {
	private final File file;
	private final String basePath;

	public JarFileSource(File file) {
		this(file, "");
	}

	private JarFileSource(File file, String basePath) {
		if (!file.exists() || !file.getName().endsWith(".jar")) {
			throw new IllegalArgumentException("No such jar file: " + file.getAbsolutePath());
		}
		this.file = file;
		this.basePath = basePath;
	}

	@Override
	public Set<ClasspathElement> getElements(String extension) throws IOException {
		JarFile jarFile = new JarFile(file);
		Enumeration<JarEntry> entries = jarFile.entries();
		Set<ClasspathElement> elements = new HashSet<ClasspathElement>();
		while (entries.hasMoreElements()) {
			JarEntry jarEntry = entries.nextElement();
			String name = jarEntry.getName();
			if (name.endsWith(extension)) {
				elements.add(new JarClasspathElement(name, jarFile, jarEntry));
			}
		}
		return elements;
	}
}
