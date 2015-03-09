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

package com.github.javaparser.model;

import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.report.DumpReporter;
import com.github.javaparser.model.utils.BulkTestClass;
import com.github.javaparser.model.utils.BulkTestRunner;
import com.github.javaparser.model.utils.ElementTestWriter;
import com.github.javaparser.model.utils.TestResources;
import org.junit.Assert;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Runs source surface tests, only comparing surface API model.
 *
 * @author Didier Villevalois
 */
@RunWith(BulkTestRunner.class)
public class BinaryTest implements BulkTestClass {

	@Override
	public String testResourcesPath() {
		return "com/github/javaparser/model/binary-tests";
	}

	@Override
	public void runTest(TestResources resources) throws IOException {
		Classpath classpath = new Classpath();
		classpath.addClassFiles(resources.getStrippedRtJar());
		classpath.addSourceFiles(resources.getSource("src"));

		StringWriter reportWriter = new StringWriter();
		JavaAnalyser javaAnalyser = new JavaAnalyser(
				new AnalysisConfiguration()
						.reporter(new DumpReporter(new PrintWriter(reportWriter)))
		);
		Analysis analysis = javaAnalyser.analyse(classpath);

		String modelString = analysis.hasErrors() ? reportWriter.toString() :
				ElementTestWriter.toString(classpath.getDependencyPackages());

		String expectedModelString = resources.getResourceAsString("output.model");

		Assert.assertEquals(expectedModelString, modelString);
	}
}
