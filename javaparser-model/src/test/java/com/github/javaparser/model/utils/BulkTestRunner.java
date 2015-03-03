package com.github.javaparser.model.utils;

import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.AnalysisConfiguration;
import com.github.javaparser.model.JavaAnalyser;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.classpath.ResourceHelper;
import com.github.javaparser.model.report.DumpReporter;
import junit.framework.AssertionFailedError;
import org.junit.Assert;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public class BulkTestRunner extends Runner {

	public abstract static class BulkTestClass {

		public abstract String testResourcesPath();
	}

	private final BulkTestClass bulkTest;
	private final String testResourcesPath;
	private final List<String> directories;
	private final Description rootDescription;
	private final List<Description> scenariDescriptions = new ArrayList<Description>();

	private final ResourceHelper resourceHelper = ResourceHelper.classpathExcludingJre();

	public BulkTestRunner(Class<? extends BulkTestClass> testClass)
			throws Throwable {
		bulkTest = testClass.newInstance();

		String path = bulkTest.testResourcesPath();
		testResourcesPath = path + (path.endsWith("/") ? "" : "/");
		Set<ClasspathElement> elements = resourceHelper.listElements(testResourcesPath);

		Set<String> directorieSet = new HashSet<String>();
		for (ClasspathElement element : elements) {
			String subPath = element.getPath().substring(testResourcesPath.length());
			int slashIndex = subPath.indexOf('/');
			subPath = slashIndex == -1 ? subPath : subPath.substring(0, slashIndex);
			directorieSet.add(subPath);
		}

		this.directories = new ArrayList<String>();
		this.directories.addAll(directorieSet);

		rootDescription = Description.createSuiteDescription(bulkTest.getClass());
		for (String directory : directories) {
			scenariDescriptions.add(Description.createTestDescription(testClass, directory));
		}
		rootDescription.getChildren().addAll(scenariDescriptions);
	}

	@Override
	public Description getDescription() {
		return rootDescription;
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(rootDescription);
		for (int i = 0; i < directories.size(); i++) {
			String directory = directories.get(i);
			Description description = scenariDescriptions.get(i);
			try {
				notifier.fireTestStarted(description);
				runTest(directory);
				notifier.fireTestFinished(description);
			} catch (AssertionFailedError e) {
				notifier.fireTestAssumptionFailed(new Failure(description, e));
			} catch (IOException e) {
				notifier.fireTestFailure(new Failure(description, e));
			}
		}
		notifier.fireTestFinished(rootDescription);
	}

	private void runTest(String directory) throws IOException {
		Classpath classpath = new Classpath();
		classpath.addClassFiles(ResourceHelper.getJarResourceSource(ClassLoader.getSystemResource("rt-striped.jar")));
		classpath.addSourceFiles(resourceHelper.getSource(testResourcesPath + directory + "/src/"));

		StringWriter reportWriter = new StringWriter();
		JavaAnalyser javaAnalyser = new JavaAnalyser(
				new AnalysisConfiguration()
						.reporter(new DumpReporter(new PrintWriter(reportWriter)))
		);
		Analysis analysis = javaAnalyser.buildModel(classpath);

		String modelString = analysis.hasErrors() ? reportWriter.toString() :
				ElementTestWriter.toString(analysis.getSourcePackages());

		String expectedModelString = fromStream(
				ClassLoader.getSystemResourceAsStream(testResourcesPath + directory + "/output.model"),
				"UTF-8");

		Assert.assertEquals(expectedModelString, modelString);
	}

	public String fromStream(InputStream inputStream, String encoding)
			throws IOException {
		return new String(readFully(inputStream), encoding);
	}

	private byte[] readFully(InputStream inputStream)
			throws IOException {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length = 0;
		while ((length = inputStream.read(buffer)) != -1) {
			baos.write(buffer, 0, length);
		}
		return baos.toByteArray();
	}
}
