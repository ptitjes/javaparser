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

	public abstract E dereference();

	public static abstract class Lazy<E extends QualifiedNameableElem> extends ElemRef<E> {

		private final EltName qualifiedName;
		private E elem;

		public Lazy(EltName qualifiedName) {
			this.qualifiedName = qualifiedName;
		}

		public abstract E load();

		@Override
		public final EltName qualifiedName() {
			return qualifiedName;
		}

		@Override
		public final E dereference() {
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
		public final E dereference() {
			return elem;
		}
	}
}
