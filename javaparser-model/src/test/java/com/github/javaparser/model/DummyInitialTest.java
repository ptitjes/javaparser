package com.github.javaparser.model;

import com.github.javaparser.ParseException;
import com.github.javaparser.model.report.DumpReporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.lang.model.element.*;
import javax.lang.model.util.ElementScanner8;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author Didier Villevalois
 */
@RunWith(JUnit4.class)
public class DummyInitialTest {

	@Test
	public void initialTest() throws IOException, ParseException {
		JavaAnalyser analyser = new JavaAnalyser(
				new AnalysisConfiguration()
						.reporter(new DumpReporter(new PrintWriter(System.out)))
		);

		Analysis model = analyser.buildModel(new File("javaparser/javaparser-model/src/test/data"));

		if (!model.hasErrors()) {
			for (PackageElement packageElement : model.getPackageElements()) {
				dumpScanner.scan(packageElement);
			}
		}
	}

	private ElementScanner8<Void, Void> dumpScanner = new ElementScanner8<Void, Void>() {

		private int indent = 0;

		@Override
		public Void visitPackage(PackageElement e, Void aVoid) {
			printIndent();
			System.out.println("Package: " + e.getSimpleName());
			return super.visitPackage(e, aVoid);
		}

		@Override
		public Void visitType(TypeElement e, Void aVoid) {
			printIndent();
			System.out.print("Type: ");
			switch (e.getKind()) {
				case CLASS:
					System.out.print("class ");
					break;
				case INTERFACE:
					System.out.print("interface ");
					break;
				case ANNOTATION_TYPE:
					System.out.print("@interface ");
					break;
				case ENUM:
					System.out.print("enum ");
					break;
			}
			System.out.print(e.getSimpleName());

			System.out.print("(superClass: " + e.getSuperclass() + ", interfaces: " + e.getInterfaces().toString() + ")");

			System.out.println();
			return super.visitType(e, aVoid);
		}

		@Override
		public Void visitVariable(VariableElement e, Void aVoid) {
			printIndent();
			System.out.println("Variable: " + e.getSimpleName());
			return super.visitVariable(e, aVoid);
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Void aVoid) {
			printIndent();
			System.out.println("Executable: " + e.getSimpleName());
			return super.visitExecutable(e, aVoid);
		}

		@Override
		public Void scan(Element e, Void aVoid) {
			indent++;
			super.scan(e, aVoid);
			indent--;
			return null;
		}

		private void printIndent() {
			for (int i = 0; i < indent; i++) {
				System.out.print("  ");
			}
		}
	};
}
