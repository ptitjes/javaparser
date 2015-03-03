package com.github.javaparser.model.utils;

import com.github.javaparser.model.classpath.ResourceHelper;
import junit.framework.AssertionFailedError;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class BulkTestRunner extends Runner {

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

		directories = new ArrayList<String>(resourceHelper.listDirectories(testResourcesPath));

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

				TestResources resources = new TestResources(resourceHelper, testResourcesPath, directory);
				bulkTest.runTest(resources);

				notifier.fireTestFinished(description);
			} catch (AssertionFailedError e) {
				notifier.fireTestAssumptionFailed(new Failure(description, e));
			} catch (IOException e) {
				notifier.fireTestFailure(new Failure(description, e));
			} catch (Throwable e) {
				notifier.fireTestFailure(new Failure(description, e));
			}
		}
		notifier.fireTestFinished(rootDescription);
	}
}
