package com.github.javaparser.model;

import com.github.javaparser.model.report.NullReporter;
import com.github.javaparser.model.report.Reporter;

/**
 * An object to configure the java analysis. This is a mutable object with fluent setters.
 *
 * @author Didier Villevalois
 */
public class AnalysisConfiguration {

	private Reporter reporter = new NullReporter();
	private String encoding = null;
	private boolean considerComments = true;
	private boolean analyseCode = true;

	/**
	 * Creates a default configuration.
	 */
	public AnalysisConfiguration() {
	}

	/**
	 * Configures the parse encoding for the source files.
	 *
	 * @param encoding the parse encoding
	 * @return this configuration object
	 */
	public AnalysisConfiguration encoding(String encoding) {
		this.encoding = encoding;
		return this;
	}

	/**
	 * Configures whether to consider comments in the parse of the source files.
	 *
	 * @param considerComments whether to consider comments
	 * @return this configuration object
	 */
	public AnalysisConfiguration considerComments(boolean considerComments) {
		this.considerComments = considerComments;
		return this;
	}

	/**
	 * Configures the problem reporter to use during the analysis.
	 *
	 * @param reporter the problem reporter
	 * @return this configuration object
	 */
	public AnalysisConfiguration reporter(Reporter reporter) {
		this.reporter = reporter;
		return this;
	}

	/**
	 * Configures whether to analyse source code (or simply the declarations).
	 *
	 * @param analyseCode whether to analyse source code
	 * @return this configuration object
	 */
	public AnalysisConfiguration analyseCode(boolean analyseCode) {
		this.analyseCode = analyseCode;
		return this;
	}

	/**
	 * Returns the problem reporter to use during the analysis.
	 *
	 * @return the problem reporter to use
	 */
	public Reporter reporter() {
		return reporter;
	}

	/**
	 * Returns the parse encoding for the source files.
	 *
	 * @return the parse encoding
	 */
	public String encoding() {
		return encoding;
	}

	/**
	 * Returns whether to consider comments in the parse of the source files.
	 *
	 * @return whether to consider comments
	 */
	public boolean considerComments() {
		return considerComments;
	}

	/**
	 * Returns whether to analyse source code (or simply the declarations).
	 *
	 * @return whether to analyse source code
	 */
	public boolean analyseCode() {
		return analyseCode;
	}
}
