package com.github.javaparser.model.test;

import com.github.javaparser.model.test.B.Test;

/**
 * @author Didier Villevalois
 */
class C {
	static class C1 {
		static class ExtendsTest extends Test {
		}
	}

	static class C2 extends A {
		static class ExtendsTest extends Test {
		}
	}

	static class C3 extends A {
		static class Test {
		}

		static class ExtendsTest extends Test {
		}
	}
}
