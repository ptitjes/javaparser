package initialtest;

import initialtest.CycleDepB.*;

/**
 * @author Didier Villevalois
 */
class CycleDepA {
	static class TestA1 {
		static class InnerA1 {
		}
	}

	static class TestA2 extends TestB1 {
		static class InnerA2 extends InnerB1 {
		}
	}
}
