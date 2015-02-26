package com.github.javaparser.model.classpath;

import java.util.Set;

/**
 * Represent an element of the classpath, from which we can obtain
 * a list of ClasspathElements.
 *
 * @author Federico Tomassetti
 */
public interface ClasspathSource {

	/**
	 * Elements of the tree which are not leaves.
	 */
	Set<ClasspathSource> getSubtrees();

	/**
	 * Leaves of the trees.
	 */
	Set<ClasspathElement> getElements(String extension);
}
