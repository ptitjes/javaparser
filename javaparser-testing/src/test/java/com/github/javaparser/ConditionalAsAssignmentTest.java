package com.github.javaparser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

/**
 * @author Didier Villevalois
 */
@RunWith(JUnit4.class)
public class ConditionalAsAssignmentTest {

	@Test(expected = ParseException.class)
	public void conditionalAsAssignmentShouldFail() throws ParseException {
		JavaParser.parseExpression("(1 == 2) = 4");
	}
}
