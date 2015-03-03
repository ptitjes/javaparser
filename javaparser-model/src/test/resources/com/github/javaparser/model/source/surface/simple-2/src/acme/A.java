package acme;

// TODO Report that the method of Interface1 is not implemented
public class A implements Interface1 {

	private final String string;

	// TODO Report that string is not initialized
	public A() {
	}
}

public interface Interface1 {
	// TODO Should modifiers contain public by default on interface methods ?
	void aMethod();
}
