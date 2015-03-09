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

package com.github.javaparser.model.type;

import javax.lang.model.type.PrimitiveType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Didier Villevalois
 */
public class PrimitiveTpe extends TpeMirror implements PrimitiveType {

	public static PrimitiveTpe BOOLEAN = new PrimitiveTpe(TypeKind.BOOLEAN);
	public static PrimitiveTpe BYTE = new PrimitiveTpe(TypeKind.BYTE);
	public static PrimitiveTpe SHORT = new PrimitiveTpe(TypeKind.SHORT);
	public static PrimitiveTpe INT = new PrimitiveTpe(TypeKind.INT);
	public static PrimitiveTpe LONG = new PrimitiveTpe(TypeKind.LONG);
	public static PrimitiveTpe CHAR = new PrimitiveTpe(TypeKind.CHAR);
	public static PrimitiveTpe FLOAT = new PrimitiveTpe(TypeKind.FLOAT);
	public static PrimitiveTpe DOUBLE = new PrimitiveTpe(TypeKind.DOUBLE);

	private final TypeKind kind;

	private PrimitiveTpe(TypeKind kind) {
		this.kind = kind;
	}

	@Override
	public TypeKind getKind() {
		return kind;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitPrimitive(this, p);
	}

	@Override
	public String toString() {
		switch (kind) {
			case BOOLEAN:
				return "boolean";
			case BYTE:
				return "byte";
			case SHORT:
				return "short";
			case INT:
				return "int";
			case LONG:
				return "long";
			case CHAR:
				return "char";
			case FLOAT:
				return "float";
			case DOUBLE:
				return "double";
			default:
				return null;
		}
	}
}
