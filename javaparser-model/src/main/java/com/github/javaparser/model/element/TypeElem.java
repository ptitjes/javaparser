package com.github.javaparser.model.element;

import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltSimpleName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.DeclaredTpe;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class TypeElem extends Elem implements TypeElement {

	private final EltName qualifiedName;
	private final NestingKind nesting;
	private final List<TypeParameterElem> typeParameters = new ArrayList<TypeParameterElem>();
	private final Map<EltSimpleName, TypeElem> types = new HashMap<EltSimpleName, TypeElem>();
	private final Map<EltSimpleName, ExecutableElem> executables = new HashMap<EltSimpleName, ExecutableElem>();
	private final Map<EltSimpleName, VariableElem> variables = new HashMap<EltSimpleName, VariableElem>();

	public TypeElem(Origin origin,
	                Scope parentScope,
	                Elem enclosing,
	                Set<Modifier> modifiers,
	                EltName qualifiedName,
	                EltSimpleName simpleName,
	                ElementKind kind,
	                NestingKind nesting) {
		super(origin, parentScope, enclosing, modifiers, simpleName, kind);
		this.qualifiedName = qualifiedName;
		this.nesting = nesting;
	}

	@Override
	protected void addEnclosedElem(Elem elem) {
		EltSimpleName name = elem.getSimpleName();
		if (name.isEmpty()) return;

		switch (elem.getKind()) {
			case TYPE_PARAMETER:
				typeParameters.add((TypeParameterElem) elem);
				break;
			case CLASS:
			case INTERFACE:
			case ANNOTATION_TYPE:
			case ENUM:
				enclosed.add(elem);
				types.put(name, (TypeElem) elem);
				break;
			case CONSTRUCTOR:
			case METHOD:
			case STATIC_INIT:
			case INSTANCE_INIT:
				enclosed.add(elem);
				executables.put(name, (ExecutableElem) elem);
				break;
			case FIELD:
			case ENUM_CONSTANT:
				enclosed.add(elem);
				variables.put(name, (VariableElem) elem);
				break;
		}
	}

	@Override
	public final Name getQualifiedName() {
		return qualifiedName;
	}

	@Override
	public final NestingKind getNestingKind() {
		return nesting;
	}

	@Override
	public final List<? extends TypeParameterElement> getTypeParameters() {
		return typeParameters;
	}

	@Override
	public TypeMirror getSuperclass() {
		return null;
	}

	@Override
	public List<? extends TypeMirror> getInterfaces() {
		return null;
	}

	@Override
	public <R, P> R accept(ElementVisitor<R, P> v, P p) {
		return v.visitType(this, p);
	}

	@Override
	public TpeMirror asType() {
		List<TpeMirror> typeParametersAsTypes = new ArrayList<TpeMirror>();
		for (TypeParameterElem typeParameter : typeParameters) {
			typeParametersAsTypes.add(typeParameter.asType());
		}
		return new DeclaredTpe(getEnclosingElement().asType(), this, typeParametersAsTypes);
	}

	@Override
	public Scope scope() {
		return scope;
	}

	private Scope scope = new Scope() {
		@Override
		public Scope parentScope() {
			return TypeElem.this.parentScope();
		}

		@Override
		public TypeElem resolveLocalType(EltSimpleName name) {
			return types.get(name);
		}

		@Override
		public VariableElem resolveLocalVariable(EltSimpleName name) {
			return variables.get(name);
		}

		@Override
		public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
			return executables.get(name);
		}
	};
}
