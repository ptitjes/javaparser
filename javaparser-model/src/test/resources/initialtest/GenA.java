package initialtest;

/**
 * @author Didier Villevalois
 */
class GenA {
	interface InterfaceA {
	}

	class ClassA {
	}

	class GenA1<X/* extends Y*/, Y extends X> extends GenParent<X, Y> {
	}

	class GenA2<X/* extends Y*/, Y extends ClassA & InterfaceA> extends GenParent<X, Y> {
	}

	// Should fail because Y is union type with a type var
	//class GenA3<X/* extends Y*/, Y extends X & InterfaceA> extends GenParent<X, Y> {
	//}

	class GenParent<T, U> {
	}

	public ClassA a = new ClassA() {
		private int toto = 0;
	};
}