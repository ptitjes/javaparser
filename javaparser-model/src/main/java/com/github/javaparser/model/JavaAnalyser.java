package com.github.javaparser.model;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseException;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.model.classpath.ClasspathElement;
import com.github.javaparser.model.classpath.ClasspathSource;
import com.github.javaparser.model.classpath.DirSourcesFinder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

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
		return buildModel(new DirSourcesFinder(sourceDirectory));
	}

	public Analysis buildModel(final ClasspathSource sourceDirectory) {
		Queue<ClasspathSource> directories = new ArrayDeque<ClasspathSource>();
		List<ClasspathElement> sourceFiles = new ArrayList<ClasspathElement>();

		directories.add(sourceDirectory);
		while (!directories.isEmpty()) {
			ClasspathSource current = directories.poll();

			directories.addAll(current.getSubtrees());
			sourceFiles.addAll(current.getElements());
		}

		Analysis analysis = new Analysis(configuration);
		for (ClasspathElement sourceFile : sourceFiles) {
			try {
				CompilationUnit cu = JavaParser.parse(sourceFile.getInputStream(),
						configuration.getEncoding(),
						configuration.isConsideringComments());
				analysis.addCompilationUnit(sourceFile, cu);
			} catch (ParseException e) {
				analysis.report(sourceFile, e);
			} catch (IOException e) {
				analysis.report(sourceFile, e);
			}
		}
		analysis.proceed();
		return analysis;
	}
}
