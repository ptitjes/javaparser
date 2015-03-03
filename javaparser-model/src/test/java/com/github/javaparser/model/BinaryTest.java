package com.github.javaparser.model;

import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.report.DumpReporter;
import com.github.javaparser.model.utils.BulkTestClass;
import com.github.javaparser.model.utils.BulkTestRunner;
import com.github.javaparser.model.utils.ElementTestWriter;
import com.github.javaparser.model.utils.TestResources;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Runs source surface tests, only comparing surface API model.
 *
 * @author Didier Villevalois
 */
@RunWith(BulkTestRunner.class)
public class BinaryTest implements BulkTestClass {

	@Override
	public String testResourcesPath() {
		return "com/github/javaparser/model/binary-tests";
	}

	@Override
	public void runTest(TestResources resources) throws IOException {
		Classpath classpath = new Classpath();
		classpath.addClassFiles(resources.getStrippedRtJar());
		classpath.addSourceFiles(resources.getSource("src"));

		StringWriter reportWriter = new StringWriter();
		JavaAnalyser javaAnalyser = new JavaAnalyser(
				new AnalysisConfiguration()
						.reporter(new DumpReporter(new PrintWriter(reportWriter)))
		);
		Analysis analysis = javaAnalyser.analyse(classpath);

		String modelString = analysis.hasErrors() ? reportWriter.toString() :
				ElementTestWriter.toString(classpath.getDependencyPackages());

		String expectedModelString = resources.getResourceAsString("output.model");

		Assert.assertEquals(expectedModelString, modelString);
	}
}
