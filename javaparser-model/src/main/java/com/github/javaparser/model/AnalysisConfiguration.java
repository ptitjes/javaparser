package com.github.javaparser.model;

import com.github.javaparser.model.report.NullReporter;
import com.github.javaparser.model.report.Reporter;

/**
 * @author Didier Villevalois
 */
public class AnalysisConfiguration {

	private Reporter reporter = new NullReporter();
	private String encoding = null;
	private boolean considerComments = true;
	private boolean analyseCode = true;

	public AnalysisConfiguration() {
	}

	public AnalysisConfiguration encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	public AnalysisConfiguration considerComments(boolean considerComments) {
		this.considerComments = considerComments;
		return this;
	}

	public AnalysisConfiguration reporter(Reporter reporter) {
		this.reporter = reporter;
		return this;
	}

	public AnalysisConfiguration analyseCode(boolean analyseCode) {
		this.analyseCode = analyseCode;
		return this;
	}

	public Reporter reporter() {
		return reporter;
	}

	public String encoding() {
		return encoding;
	}

	public boolean considerComments() {
		return considerComments;
	}

	public boolean analyseCode() {
		return analyseCode;
	}
}
