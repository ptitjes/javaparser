package com.github.javaparser.model.classpath;

import java.io.IOException;
import java.io.InputStream;

/**
 * An element on the classpath.
 *
 * @author Federico Tomassetti
 */
public abstract class ClasspathElement {
	private final String path;

	public ClasspathElement(String path) {
		this.path = path;
	}

	public final String getPath() {
		return this.path;
	}

	public abstract InputStream getInputStream() throws IOException;
}
