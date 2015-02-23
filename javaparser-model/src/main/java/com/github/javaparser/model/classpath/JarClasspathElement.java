package com.github.javaparser.model.classpath;

import java.io.IOException;
import java.io.InputStream;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Element of the Classpath obtained by looking into a jar.
 *
 * @author Federico Tomassetti
 */
class JarClasspathElement extends ClasspathElement {
	private JarFile jarFile;
	private JarEntry jarEntry;

	public JarClasspathElement(String path, JarFile jarFile, JarEntry jarEntry) {
		super(path);
		this.jarFile = jarFile;
		this.jarEntry = jarEntry;
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return jarFile.getInputStream(jarEntry);
	}
}
