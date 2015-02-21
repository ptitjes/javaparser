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
		FileFilter javaSourceFilter = new FileFilter() {
			@Override
			public boolean accept(File file) {
				return file.getName().endsWith(".java");
			}
		};

		Queue<File> directories = new ArrayDeque<File>();
		List<File> sourceFiles = new ArrayList<File>();

		directories.add(sourceDirectory);
		while (!directories.isEmpty()) {
			File current = directories.poll();

			directories.addAll(Arrays.asList(current.listFiles(directoryFilter)));
			sourceFiles.addAll(Arrays.asList(current.listFiles(javaSourceFilter)));
		}

		List<CompilationUnit> compilationUnits = new ArrayList<CompilationUnit>();
		for (File sourceFile : sourceFiles) {
			compilationUnits.add(JavaParser.parse(sourceFile, encoding, considerComments));
		}

		return buildModel(compilationUnits);
	}

	public Analysis buildModel(final List<CompilationUnit> compilationUnits) {
		Analysis analysis = new Analysis(compilationUnits);
		analysis.run();
		return analysis;
	}
}
