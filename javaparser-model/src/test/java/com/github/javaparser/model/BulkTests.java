package com.github.javaparser.model;

import com.github.javaparser.model.utils.BulkTestRunner;
import com.github.javaparser.model.utils.BulkTestRunner.BulkTestClass;
import org.junit.runner.RunWith;

/**
 * @author Didier Villevalois
 */
@RunWith(BulkTestRunner.class)
public class BulkTests extends BulkTestClass {

	@Override
	public String testResourcesPath() {
		return "javaparser-model-tests/bulk";
	}
}
