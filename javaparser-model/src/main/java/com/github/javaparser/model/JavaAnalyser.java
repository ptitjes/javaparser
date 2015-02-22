package com.github.javaparser.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.report.NullReporter;
import com.github.javaparser.model.report.Reporter;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class JavaAnalyser {

	private final AnalysisConfiguration configuration;

	public JavaAnalyser() {
		this(new AnalysisConfiguration());
	}

	public JavaAnalyser(AnalysisConfiguration configuration) {
		this.configuration = configuration;
	}

	public Analysis buildModel(final File sourceDirectory) {
		if (!sourceDirectory.exists())
			throw new IllegalArgumentException("No such directory: " + sourceDirectory.getAbsolutePath());

		FileFilter directoryFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.isDirectory();
			}
		};
		FileFilter packageSourceFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String name = file.getName();
				return name.equals("package.java");
			}
		};
		FileFilter javaSourceFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				String name = file.getName();
				return name.endsWith(".java") && !name.equals("package.java");
			}
		};

		Queue<File> directories = new ArrayDeque<File>();
		List<File> sourceFiles = new ArrayList<File>();

		directories.add(sourceDirectory);
		while (!directories.isEmpty()) {
			File current = directories.poll();

			directories.addAll(Arrays.asList(current.listFiles(directoryFilter)));
			sourceFiles.addAll(Arrays.asList(current.listFiles(packageSourceFilter)));
			sourceFiles.addAll(Arrays.asList(current.listFiles(javaSourceFilter)));
		}

		Analysis analysis = new Analysis(configuration);
		for (File sourceFile : sourceFiles) {
			try {
				CompilationUnit cu = JavaParser.parse(sourceFile,
						configuration.getEncoding(),
						configuration.isConsiderComments());
				analysis.addCompilationUnit(sourceFile, cu);
			} catch (ParseException e) {
				analysis.report(sourceFile, e);
			} catch (IOException e) {
				analysis.report(sourceFile, e);
			}
		}
		return analysis;
	}
}
