package com.github.javaparser.model;

import com.github.javaparser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.lang.model.element.Element;
import javax.lang.model.element.PackageElement;
import javax.lang.model.util.ElementScanner8;
import java.io.File;
import java.io.IOException;

/**
 * @author Didier Villevalois
 */
@RunWith(JUnit4.class)
public class DummyInitialTest {

	@Test
	public void initialTest() throws IOException, ParseException {
		JavaAnalyser analyser = new JavaAnalyser();
		Analysis model = analyser.buildModel(new File("javaparser/javaparser-core/src/main/java"));

		ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {

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

		for (PackageElement packageElement : model.getPackageElements()) {
			scanner.scan(packageElement);
		}
	}
}
