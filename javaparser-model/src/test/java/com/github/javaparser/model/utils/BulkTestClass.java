package com.github.javaparser.model.utils;

import java.io.IOException;

/**
 * @author Didier Villevalois
 */
public interface BulkTestClass {

	String testResourcesPath();

	void runTest(TestResources resources) throws IOException;
}
