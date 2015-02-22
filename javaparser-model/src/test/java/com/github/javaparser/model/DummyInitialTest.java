package com.github.javaparser.model;

import com.github.javaparser.ParseException;
import com.github.javaparser.model.report.DumpReporter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
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
		public Void scan(Element e, Void aVoid) {
			for (int i = 0; i < indent; i++) {
				System.out.print("  ");
			}
			System.out.println(e.getKind() + " " + e.getSimpleName());
			indent++;
			super.scan(e, aVoid);
			indent--;
			return null;
		}
	};
}
