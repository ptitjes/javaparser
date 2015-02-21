package com.github.javaparser.model;

import com.github.javaparser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.IOException;

/**
 * @author Didier Villevalois
 */
@RunWith(JUnit4.class)
public class DummyInitialTest {

	@Test
	public void initialTest() throws IOException, ParseException {
		JavaAnalyser analyser = new JavaAnalyser();
		Analysis model = analyser.buildModel(new File("javaparser/javaparser-core/src/main/java"));
	}
}
