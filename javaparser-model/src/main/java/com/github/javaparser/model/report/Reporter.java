package com.github.javaparser.model.report;

import com.github.javaparser.model.element.Origin;

import java.io.File;

/**
 * @author Didier Villevalois
 */
public interface Reporter {

	public enum Severity {
		INFO, WARNING, ERROR
	}

	public void report(File file, Exception exception);

	public void report(Severity severity, String message, Origin origin);
}
