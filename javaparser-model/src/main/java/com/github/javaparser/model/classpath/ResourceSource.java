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

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Retrieves ClasspathElement from a directory.
 *
 * @author Federico Tomassetti
 */
public class ResourceSource implements ClasspathSource {
	private final ResourceHelper helper;
	private final String path;
	private final String basePath;

	public ResourceSource(ResourceHelper helper, String path) {
		this(helper, path, "");
	}

	private ResourceSource(ResourceHelper helper, String path, String basePath) {
		this.helper = helper;
		this.path = path;
		this.basePath = basePath;
	}

	@Override
	public Set<ClasspathElement> getElements(String extension) throws IOException {
		Set<ClasspathElement> elements = helper.listElements(path);
		Set<ClasspathElement> filteredElements = new HashSet<ClasspathElement>();
		for (ClasspathElement element : elements) {
			if (element.getPath().endsWith(extension)) {
				filteredElements.add(element);
			}
		}
		return filteredElements;
	}
}
