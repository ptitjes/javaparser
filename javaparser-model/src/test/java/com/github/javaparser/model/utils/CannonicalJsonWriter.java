package com.github.javaparser.model.utils;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public abstract class CannonicalJsonWriter<T> {

	private final PrintWriter out;
	private int indent = 0;
	private boolean lastIsNewLine = false;
	private boolean propToClose = false;

	public CannonicalJsonWriter(PrintWriter out) {
		this.out = out;
	}

	public abstract void write(T object);

	public void write(T[] objects) {
		write(Arrays.asList(objects));
	}

	public void write(Collection<? extends T> objects) {
		if (objects.isEmpty()) {
			print('[');
			print(']');
			printNewLine();
		} else {
			beginArray();
			boolean first = true;
			for (T object : new ArrayList<T>(objects)) {
				if (first) first = false;
				else {
					out.append(',');
					printNewLine();
				}
				write(object);
			}
			endArray();
		}
	}

	private void printIndent() {
		for (int i = 0; i < indent; i++) {
			out.append("  ");
		}
	}

	private void printNewLine() {
		lastIsNewLine = true;
	}

	protected void print(String string) {
		beforePrint();
		out.print(string);
	}

	private void print(char c) {
		beforePrint();
		out.print(c);
	}

	private void beforePrint() {
		if (lastIsNewLine) {
			lastIsNewLine = false;
			if (propToClose) {
				propToClose = false;
				out.append(',');
			}
			out.append('\n');
			printIndent();
		}
	}

	protected void beginObject() {
		print('{');
		indent++;
		printNewLine();
	}

	protected void endObject() {
		indent--;
		printNewLine();
		propToClose = false;
		print('}');
		printNewLine();
	}

	protected void beginArray() {
		print('[');
		indent++;
		propToClose = false;
		printNewLine();
	}

	protected void endArray() {
		indent--;
		printNewLine();
		print(']');
		printNewLine();
		propToClose = true;
	}

	protected void nameHeader(String name) {
		print(name);
		print(':');
		print(' ');
		propToClose = true;
	}

	protected void nameValue(String name, boolean value) {
		nameHeader(name);
		print(value ? "true" : "false");
		printNewLine();
	}

	protected void nameValue(String name, char value) {
		nameHeader(name);
		print('"');
		print(value);
		print('"');
		printNewLine();
	}

	protected void nameValue(String name, short value) {
		nameHeader(name);
		out.print(value);
		printNewLine();
	}

	protected void nameValue(String name, int value) {
		nameHeader(name);
		out.print(value);
		printNewLine();
	}

	protected void nameValue(String name, long value) {
		nameHeader(name);
		out.print(value);
		printNewLine();
	}

	protected void nameValue(String name, float value) {
		nameHeader(name);
		out.print(value);
		printNewLine();
	}

	protected void nameValue(String name, double value) {
		nameHeader(name);
		out.print(value);
		printNewLine();
	}

	protected void nameValue(String name, String value) {
		nameHeader(name);
		print('"');
		out.print(value);
		print('"');
		printNewLine();
	}

	protected void nameValue(String name, T value) {
		nameHeader(name);
		write(value);
		printNewLine();
	}

	protected void nameValue(String name, Collection<? extends T> value) {
		nameHeader(name);
		write(value);
		printNewLine();
	}

	protected void nameValueString(String name, Object value) {
		nameHeader(name);
		print('"');
		print(value.toString());
		print('"');
		printNewLine();
	}

	protected void nameValueString(String name, Collection<? extends Object> value) {
		nameHeader(name);
		if (value.isEmpty()) {
			print('[');
			print(']');
			printNewLine();
		} else {
			beginArray();
			boolean first = true;
			for (Object object : value) {
				if (first) first = false;
				else {
					out.append(',');
					printNewLine();
				}
				print('"');
				print(object.toString());
				print('"');
			}
			endArray();
		}
		printNewLine();
	}
}
