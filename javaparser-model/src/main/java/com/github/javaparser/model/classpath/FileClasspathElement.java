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
