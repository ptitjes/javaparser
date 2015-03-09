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

import com.github.javaparser.model.element.TypeParameterElem;

import javax.lang.model.element.Element;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.TypeVariable;
import javax.lang.model.type.TypeVisitor;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class TpeVariable extends TpeMirror implements TypeVariable {

	private final TypeParameterElem element;
	private final TpeMirror java_lang_Object;
	private final TpeMirror upperBound;
	private final TpeMirror lowerBound;

	public TpeVariable(TypeParameterElem element,
	                   TpeMirror upperBound,
	                   TpeMirror lowerBound) {
		this.element = element;
		this.java_lang_Object = null;
		this.upperBound = upperBound;
		this.lowerBound = lowerBound;
	}

	public TpeVariable(TypeParameterElem element, TpeMirror java_lang_Object) {
		this.element = element;
		this.java_lang_Object = java_lang_Object;
		this.upperBound = null;
		this.lowerBound = null;
	}

	@Override
	public Element asElement() {
		return element;
	}

	@Override
	public TypeMirror getUpperBound() {
		if (upperBound != null) return upperBound;
		else {
			List<TpeMirror> bounds = element.getBounds();
			if (bounds.isEmpty()) return java_lang_Object;
			else return new UnionTpe(bounds);
		}
	}

	@Override
	public TypeMirror getLowerBound() {
		if (upperBound != null) return upperBound;
		else return NullTpe.NULL;
	}

	@Override
	public TypeKind getKind() {
		return TypeKind.TYPEVAR;
	}

	@Override
	public <R, P> R accept(TypeVisitor<R, P> typeVisitor, P p) {
		return typeVisitor.visitTypeVariable(this, p);
	}

	@Override
	public String toString() {
		// TODO add bounds
		return element.getSimpleName().toString();
	}
}
