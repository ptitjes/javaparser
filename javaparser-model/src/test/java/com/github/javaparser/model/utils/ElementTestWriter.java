/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2015 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaParser.  If not, see <http://www.gnu.org/licenses/>.
 */

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
			nameValueString("modifiers", e.getModifiers());
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
			nameValueString("modifiers", e.getModifiers());
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
			nameValueString("modifiers", e.getModifiers());
			nameValueString("simpleName", e.getSimpleName());
			nameValueString("bounds", e.getBounds());
			endObject();
			return null;
		}

		@Override
		public Void visitVariable(VariableElement e, Void aVoid) {
			beginObject();
			nameValueString("kind", e.getKind());
			nameValueString("modifiers", e.getModifiers());
			nameValueString("simpleName", e.getSimpleName());
			nameValueString("type", e.asType());
			endObject();
			return null;
		}

		@Override
		public Void visitExecutable(ExecutableElement e, Void aVoid) {
			beginObject();
			nameValueString("kind", e.getKind());
			nameValueString("modifiers", e.getModifiers());
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
