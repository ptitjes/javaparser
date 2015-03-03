package com.github.javaparser.model;

import com.github.javaparser.ParseException;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ResourceHelper;
import com.github.javaparser.model.element.ElementUtils;
import com.github.javaparser.model.report.DumpReporter;
import com.github.javaparser.model.utils.ElementTestWriter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.ElementScanner8;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * @author Didier Villevalois
 */
@RunWith(JUnit4.class)
public class DummyInitialTest {

	@Test
	public void initialTest() throws IOException, ParseException {
		JavaAnalyser analyser = new JavaAnalyser(
				new AnalysisConfiguration()
						.reporter(new DumpReporter(new PrintWriter(System.out)))
		);

		Classpath classpath = new Classpath();
		classpath.addSourceFileDirectory(new File("../javaparser-core/src/main/java"));
		classpath.addSourceFileDirectory(new File("../javaparser-core/target/generated-sources/javacc"));
		classpath.addClassFiles(ResourceHelper.getJarResourceSource(ClassLoader.getSystemResource("rt-striped.jar")));

//		Analysis model = analyser.buildModel(classpath);
//		ElementUtils elementUtils = model.getElementUtils();
//
//		TypeElement enumTypeElem = elementUtils.getTypeElement("java.lang.Enum");
//
//		System.out.println(System.getProperty("java.class.path"));
//
//		System.out.println(ElementTestWriter.toString(enumTypeElem));

//		if (!model.hasErrors()) {
//			System.out.println(ElementTestWriter.toString(com.getSourcePackages()));
//		}
	}
}
