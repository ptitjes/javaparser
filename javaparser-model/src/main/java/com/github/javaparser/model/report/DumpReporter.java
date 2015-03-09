/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2015 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaParser.  If not, see <http://www.gnu.org/licenses/>.
 */

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
	public void report(String message, Exception exception) {
		errors = true;
		report(Severity.ERROR, message, exception.getMessage());
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
