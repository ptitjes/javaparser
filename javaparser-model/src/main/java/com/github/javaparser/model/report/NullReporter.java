package com.github.javaparser.model.report;

import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.element.Origin;

/**
 * @author Didier Villevalois
 */
public class NullReporter implements Reporter {
	@Override
	public void report(ClasspathElement file, Exception exception) {
	}

	@Override
	public void report(Severity severity, String message, Origin origin) {
	}

	@Override
	public boolean hasErrors() {
		return false;
	}
}
