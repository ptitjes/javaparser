package com.github.javaparser.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class JavaAnalyser {

	public Analysis buildModel(final File sourceDirectory)
			throws ParseException, IOException {
		return buildModel(sourceDirectory, null, true);
	}

	public Analysis buildModel(final File sourceDirectory, final String encoding)
			throws ParseException, IOException {
		return buildModel(sourceDirectory, encoding, true);
	}

	public Analysis buildModel(final File sourceDirectory, final String encoding, boolean considerComments)
			throws ParseException, IOException {
		if (!sourceDirectory.exists())
			throw new IllegalArgumentException();

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

		Analysis analysis = new Analysis();
		for (File sourceFile : sourceFiles) {
			analysis.addCompilationUnit(JavaParser.parse(sourceFile, encoding, considerComments));
		}
		return analysis;
	}
}
