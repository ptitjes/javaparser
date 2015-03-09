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

import javax.lang.model.type.NoType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeVisitor;

/**
 * @author Didier Villevalois
 */
public class NoTpe extends TpeMirror implements NoType {

	public static NoTpe NONE = new NoTpe(TypeKind.NONE);
	public static NoTpe VOID = new NoTpe(TypeKind.VOID);
	public static NoTpe PACKAGE = new NoTpe(TypeKind.PACKAGE);

	private final TypeKind kind;

	private NoTpe(TypeKind kind) {
		this.kind = kind;
	}

	@Override
	public TypeKind getKind() {
		return kind;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitNoType(this, p);
	}

	@Override
	public String toString() {
		switch (kind) {
			case NONE:
				return "_bottom_";
			case VOID:
				return "void";
			case PACKAGE:
				return "_package_";
			default:
				return null;
		}
	}
}
