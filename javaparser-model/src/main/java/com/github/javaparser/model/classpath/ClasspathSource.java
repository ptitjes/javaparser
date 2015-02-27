package com.github.javaparser.model.classpath;

import java.io.IOException;
import java.util.Set;

/**
 * Represent an element of the classpath, from which we can obtain
 * a list of ClasspathElements.
 *
 * @author Federico Tomassetti
 */
public interface ClasspathSource {

	/**
	 * Leaves of the trees.
	 */
	Set<ClasspathElement> getElements(String extension) throws IOException;
}
