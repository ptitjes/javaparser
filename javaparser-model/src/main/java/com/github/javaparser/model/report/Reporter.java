package com.github.javaparser.model.report;

import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.element.Origin;

/**
 * @author Didier Villevalois
 */
public interface Reporter {

	public enum Severity {
		INFO, WARNING, ERROR
	}

	public void report(ClasspathElement file, Exception exception);

	public void report(Severity severity, String message, Origin origin);

	boolean hasErrors();
}
