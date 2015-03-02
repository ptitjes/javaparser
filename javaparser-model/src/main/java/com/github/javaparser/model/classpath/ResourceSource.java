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
