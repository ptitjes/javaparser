package com.acme;

import java.util.List;

/* This file uses tabs and must not be reformatted. */
public class TestClass {
    /** New Method doc addition */
    public boolean newMethod();

	// Dangling 1!
// Dangling 2!

	// Dangling 3!

    // Cool 1
	// Oh yeah 1!
	// Oh yeah 2!
    // Cool 2
    /** Doc addition */
	public static String method1(List<String> strings){
		StringBuilder buffer =  
new StringBuilder();
		for (String string:strings) {
			/* yes */buffer.append(string);
		}
		return buffer.toString();// Trailing 1
	} // Trailing 2
}
