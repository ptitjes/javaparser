package com.github.javaparser.model.report;

import com.github.javaparser.model.element.Origin;

import java.io.File;

/**
 * @author Didier Villevalois
 */
public class NullReporter implements Reporter {
	@Override
	public void report(File file, Exception exception) {
	}

	@Override
	public void report(Severity severity, String message, Origin origin) {
	}
}
