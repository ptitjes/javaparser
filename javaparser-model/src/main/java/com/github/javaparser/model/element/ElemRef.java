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

package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltName;

/**
 * @author Didier Villevalois
 */
public abstract class ElemRef<E extends QualifiedNameableElem> {

	public static <E extends QualifiedNameableElem> ElemRef<E> make(E elem) {
		return new Hard<E>(elem);
	}

	public abstract EltName qualifiedName();

	public abstract E dereference() throws ElemDereferenceException;

	public static abstract class Lazy<E extends QualifiedNameableElem> extends ElemRef<E> {

		private final EltName qualifiedName;
		private E elem;

		public Lazy(EltName qualifiedName) {
			this.qualifiedName = qualifiedName;
		}

		public abstract E load() throws ElemDereferenceException;

		@Override
		public final EltName qualifiedName() {
			return qualifiedName;
		}

		@Override
		public final E dereference() throws ElemDereferenceException {
			if (elem == null) {
				elem = load();
			}
			return elem;
		}
	}

	private static class Hard<E extends QualifiedNameableElem> extends ElemRef<E> {

		private final E elem;

		public Hard(E elem) {
			this.elem = elem;
		}

		@Override
		public EltName qualifiedName() {
			return elem.getQualifiedName();
		}

		@Override
		public final E dereference() throws ElemDereferenceException {
			return elem;
		}
	}
}
