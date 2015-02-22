package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.NoTpe;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ElementVisitor;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.PackageElement;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public class PackageElem extends Elem implements PackageElement {

	private final EltName qualifiedName;
	private final Map<EltSimpleName, TypeElem> types = new HashMap<EltSimpleName, TypeElem>();

	public PackageElem(Scope parentScope, Origin origin, EltName qualifiedName) {
		super(origin, parentScope, null, EnumSet.noneOf(Modifier.class), qualifiedName.simpleName(), ElementKind.PACKAGE);
		this.qualifiedName = qualifiedName;
	}

	@Override
	protected void addEnclosedElem(Elem elem) {
		EltSimpleName name = elem.getSimpleName();
		if (name.isEmpty()) return;

		switch (elem.getKind()) {
			case CLASS:
			case INTERFACE:
			case ANNOTATION_TYPE:
			case ENUM:
				enclosed.add(elem);
				types.put(name, (TypeElem) elem);
				break;
		}
	}

	@Override
	public EltName getQualifiedName() {
		return qualifiedName;
	}

	@Override
	public boolean isUnnamed() {
		return qualifiedName.isEmpty();
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> elementVisitor, P p) {
		return elementVisitor.visitPackage(this, p);
	}

	@Override
	public TpeMirror asType() {
		return NoTpe.PACKAGE;
	}

	@Override
	public Scope scope() {
		return scope;
	}

	private Scope scope = new Scope() {
		@Override
		public Scope parentScope() {
			return PackageElem.this.parentScope();
		}

		@Override
		public TypeElem resolveLocalType(EltSimpleName name) {
			return types.get(name);
		}

		@Override
		public VariableElem resolveLocalVariable(EltSimpleName name) {
			return null;
		}

		@Override
		public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
			return null;
		}
	};
}
