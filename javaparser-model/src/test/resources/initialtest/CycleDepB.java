package initialtest;

import initialtest.CycleDepA.*;

/**
 * @author Didier Villevalois
 */
class CycleDepB {
	static class TestB1 {
		static class InnerB1 {
		}
	}

	static class TestB2 extends TestA1 {
		static class InnerB2 extends InnerA1 {
		}
	}
}
