package com.github.javaparser.model;

import com.github.javaparser.ParseException;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.classpath.ResourceHelper;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.report.DumpReporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.lang.model.element.Element;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.regex.Pattern;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author Federico Tomassetti
 */
@RunWith(JUnit4.class)
public class BasicTest {

	@Test
	public void initialTest() throws IOException, ParseException {

		ResourceHelper resourceHelper = new ResourceHelper();
		resourceHelper.ignore(Pattern.compile(".*/jre/.*"));

		for (ClasspathElement element : resourceHelper.listElements("initialtest")) {
			System.out.println(" - " + element.getPath());
		}

		JavaAnalyser analyser = new JavaAnalyser(
				new AnalysisConfiguration()
						.reporter(new DumpReporter(new PrintWriter(System.out)))
		);

		Analysis model = analyser.buildModel(new File("src/test/resources/scenario_a"));

		assertEquals(1, model.getCompilationUnits().size());
		assertEquals(1, model.getSourcePackages().size());
		assertTrue(model.getSourcePackages().get(0).isUnnamed());

		assertEquals(1, model.getSourcePackages().get(0).getEnclosedElements().size());
		Element elmtClassA = model.getSourcePackages().get(0).getEnclosedElements().get(0);
		assertTrue(elmtClassA instanceof TypeElem);
		TypeElem typeClassA = (TypeElem) elmtClassA;
		assertTrue(elmtClassA.getSimpleName().contentEquals("A"));
	}


}
