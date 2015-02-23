package com.github.javaparser.model.report;

import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.element.Origin;

import java.io.PrintWriter;
import java.util.StringTokenizer;

/**
 * @author Didier Villevalois
 */
public class DumpReporter implements Reporter {

	private static final String INDENT = "    ";

	private final PrintWriter out;
	private boolean errors = false;

	public DumpReporter(PrintWriter out) {
		this.out = out;
	}

	@Override
	public void report(ClasspathElement file, Exception exception) {
		errors = true;
		report(Severity.ERROR, file.getPath(), exception.getMessage());
	}

	@Override
	public void report(Severity severity, String message, Origin origin) {
		if (severity == Reporter.Severity.ERROR) errors = true;
		report(severity, origin.toLocationString(), message);
	}

	@Override
	public boolean hasErrors() {
		return errors;
	}

	private void report(Severity severity, String location, String message) {
		out.printf("%s: %s\n%s\n",
				severityToString(severity),
				location,
				indentLines(message));
		out.flush();
	}

	private String severityToString(Severity severity) {
		switch (severity) {
			case INFO:
				return "Info";
			case WARNING:
				return "Warning";
			case ERROR:
				return "Error";
			default:
				return null;
		}
	}

	private String indentLines(String message) {
		StringBuffer buffer = new StringBuffer();
		StringTokenizer tokenizer = new StringTokenizer(message, "\n");

		buffer.append(INDENT);
		buffer.append(tokenizer.nextToken());
		while (tokenizer.hasMoreElements()) {
			buffer.append("\n");
			buffer.append(INDENT);
			buffer.append(tokenizer.nextToken());
		}
		return buffer.toString();
	}
}
