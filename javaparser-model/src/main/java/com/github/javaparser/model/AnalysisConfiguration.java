package com.github.javaparser.model;

import com.github.javaparser.model.report.NullReporter;
import com.github.javaparser.model.report.Reporter;

/**
 * @author Didier Villevalois
 */
public class AnalysisConfiguration {

	private Reporter reporter = new NullReporter();
	private String encoding = null;
	private boolean consideringComments = true;

	public AnalysisConfiguration() {
	}

	public AnalysisConfiguration encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public AnalysisConfiguration considerComments(boolean considerComments) {
		this.consideringComments = considerComments;
		return this;
	}

	public AnalysisConfiguration reporter(Reporter reporter) {
		this.reporter = reporter;
		return this;
	}

	public Reporter getReporter() {
		return reporter;
	}

	public String getEncoding() {
		return encoding;
	}

	public boolean isConsideringComments() {
		return consideringComments;
	}
}
