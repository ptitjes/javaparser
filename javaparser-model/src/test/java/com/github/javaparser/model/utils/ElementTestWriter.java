package com.github.javaparser.model.utils;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.AbstractElementVisitor8;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class ElementTestWriter extends CannonicalJsonWriter<Element> {

	public static String toString(Element element) {
		StringWriter writer = new StringWriter();
		new ElementTestWriter(new PrintWriter(writer)).write(element);
		return writer.toString();
	}

	public static String toString(List<? extends Element> elements) {
		StringWriter writer = new StringWriter();
		new ElementTestWriter(new PrintWriter(writer)).write(elements);
		return writer.toString();
	}

	public ElementTestWriter(PrintWriter out) {
		super(out);
	}

	public void write(Element elem) {
		dumpScanner.visit(elem);
	}

	private AbstractElementVisitor8<Void, Void> dumpScanner = new AbstractElementVisitor8<Void, Void>() {

		@Override
		public Void visitPackage(PackageElement e, Void aVoid) {
			beginObject();
			nameValueString("kind", e.getKind());
			nameValueString("qualifiedName", e.getQualifiedName());
			nameValueString("simpleName", e.getSimpleName());
			nameValue("enclosedElements", e.getEnclosedElements());
			endObject();
			return null;
		}

		@Override
		public Void visitType(TypeElement e, Void aVoid) {
			beginObject();
			nameValueString("kind", e.getKind());
			nameValueString("qualifiedName", e.getQualifiedName());
			nameValueString("simpleName", e.getSimpleName());
			nameValue("typeParameters", e.getTypeParameters());
			nameValueString("superClass", e.getSuperclass());
			nameValueString("interfaces", e.getInterfaces());
			nameValue("enclosedElements", e.getEnclosedElements());
			endObject();
			return null;
		}

		@Override
		public Void visitTypeParameter(TypeParameterElement e, Void aVoid) {
			beginObject();
			nameValueString("kind", e.getKind());
			nameValueString("simpleName", e.getSimpleName());
			nameValueString("bounds", e.getBounds());
			endObject();
			return null;
		}

		@Override
		public Void visitVariable(VariableElement e, Void aVoid) {
			beginObject();
			nameValueString("kind", e.getKind());
			nameValueString("simpleName", e.getSimpleName());
			nameValueString("type", e.asType());
			endObject();
			return null;
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Void aVoid) {
			beginObject();
			nameValueString("kind", e.getKind());
			nameValueString("simpleName", e.getSimpleName());
			nameValue("typeParameters", e.getTypeParameters());
			nameValueString("returnType", e.getReturnType());
			nameValue("parameters", e.getParameters());
			nameValueString("thrownTypes", e.getThrownTypes());
			endObject();
			return null;
		}
	};
}
