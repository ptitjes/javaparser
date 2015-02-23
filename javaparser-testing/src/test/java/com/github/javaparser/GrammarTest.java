package com.github.javaparser;

import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.expr.AnnotationExpr;
import com.github.javaparser.ast.expr.MarkerAnnotationExpr;
import com.github.javaparser.ast.expr.NameExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Arrays;
import java.util.Collections;

@RunWith(JUnit4.class)
public class GrammarTest {

/*
	// Valid Java 8 code

	@Target(ElementType.TYPE_USE)
	@interface Ann1 {
	}

	@Target(ElementType.TYPE_USE)
	@interface Ann2 {
	}

	class B {
		class C {
		}

		class A extends @Ann1 B.@Ann2 C {
		}
	}
*/

	@Test
	public void parseClassAndDump() {
		parsing("class A extends @Ann1 B.@Ann2 C {\n}")
				.asBodyDeclaration()
				.succeedsEqual();
	}

	@Test
	public void parseClass() {
		parsing("class A extends @Ann1 B.@Ann2 C {\n}").asBodyDeclaration().succeedsWith(
				new ClassOrInterfaceDeclaration(0,
						null,
						false,
						"A",
						null,
						Arrays.asList(
								annotated(
										new ClassOrInterfaceType(
												annotated(
														new ClassOrInterfaceType("B"),
														new MarkerAnnotationExpr(new NameExpr("Ann1"))
												),
												"C"
										),
										new MarkerAnnotationExpr(new NameExpr("Ann2"))
								)
						),
						null,
						null
				)
		);
	}

	// Test DSL

	public ClassOrInterfaceType annotated(ClassOrInterfaceType type, AnnotationExpr expr) {
		type.setAnnotations(Collections.singletonList(expr));
		return type;
	}

	protected Parsing parsing(String content) {
		return new Parsing(content);
	}

	public class Parsing {

		private final String content;

		public Parsing(String content) {
			this.content = content;
		}

		public ParseResult asBodyDeclaration() {
			return new ParseResult(content) {
				@Override
				protected Node parse(String content) throws ParseException {
					return JavaParser.parseBodyDeclaration(content);
				}
			};
		}
	}

	public abstract class ParseResult {

		private final String content;

		public ParseResult(String content) {
			this.content = content;
		}

		protected abstract Node parse(String content) throws ParseException;

		public void succeedsWith(Node expected) {
			try {
				Assert.assertEquals(expected, parse(content));
			} catch (ParseException e) {
				Assert.fail(e.getMessage());
			}
		}

		public void succeedsWith(String expected) {
			try {
				Assert.assertEquals(expected, parse(content).toString());
			} catch (ParseException e) {
				Assert.fail(e.getMessage());
			}
		}

		public void succeedsEqual() {
			succeedsWith(content);
		}
	}
}


