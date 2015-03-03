package acme;

// TODO Report that the method of Interface1 is not implemented
public class A implements Interface1 {

	private final String string;

	// TODO Report that string is not initialized
	public A() {
	}

	public static void staticMethodWithArgs(int intArg, String stringArg) {
	}
}

public interface Interface1 {
	// TODO Should modifiers contain public by default on interface methods ?
	void aMethod();

	// TODO Report invalid static method in interface
	static void invalidStaticMethod() {
	}
}

public interface Interface2<I extends Interface2<I>> {

}
