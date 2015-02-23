package com.github.javaparser.model.classpath;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Element of the Classpath obtained by observing a directory.
 *
 * @author Federico Tomassetti
 */
class FileClasspathElement extends ClasspathElement {
	private File file;

	public FileClasspathElement(String path, File file) {
		super(path);
		if (file == null) {
			throw new NullPointerException();
		}
		this.file = file;
	}

	@Override
	public String toString() {
		return "FileClasspathElement{" +
				"path=" + getPath() +
				" file=" + file +
				'}';
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;

		FileClasspathElement that = (FileClasspathElement) o;

		if (!file.equals(that.file)) return false;
		if (!getPath().equals(that.getPath())) return false;

		return true;
	}

	@Override
	public int hashCode() {
		return file.hashCode() + 7 * getPath().hashCode();
	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new FileInputStream(file);
	}
}
