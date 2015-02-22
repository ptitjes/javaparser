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
	private final Map<EltSimpleName, TypeParameterElem> perNameTypeParameters = new HashMap<EltSimpleName, TypeParameterElem>();
	private final Map<EltSimpleName, TypeElem> types = new HashMap<EltSimpleName, TypeElem>();
	private final Map<EltSimpleName, ExecutableElem> executables = new HashMap<EltSimpleName, ExecutableElem>();
	private final Map<EltSimpleName, VariableElem> variables = new HashMap<EltSimpleName, VariableElem>();
	private TpeMirror superClass;
	private List<TpeMirror> interfaces;

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
				perNameTypeParameters.put(name, (TypeParameterElem) elem);
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
	public final EltName getQualifiedName() {
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
		return superClass;
	}

	public void setSuperClass(TpeMirror superClass) {
		this.superClass = superClass;
	}

	@Override
	public List<? extends TypeMirror> getInterfaces() {
		return interfaces;
	}

	public void setInterfaces(List<TpeMirror> interfaces) {
		this.interfaces = interfaces;
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
		public TypeParameterElem resolveLocalTypeParameter(EltSimpleName name) {
			return perNameTypeParameters.get(name);
		}

		@Override
		public TypeElem resolveLocalType(EltSimpleName name) {
			TypeElem typeElem = types.get(name);
			if (typeElem == null && superClass != null && superClass instanceof DeclaredTpe) {
				typeElem = ((DeclaredTpe) superClass).asElement().scope().resolveLocalType(name);
			}
			if (typeElem == null && interfaces != null && !interfaces.isEmpty()) {
				for (TpeMirror anInterface : interfaces) {
					if (anInterface instanceof DeclaredTpe) {
						typeElem = ((DeclaredTpe) anInterface).asElement().scope().resolveLocalType(name);
						if (typeElem != null) break;
					}
				}
			}
			return typeElem;
		}

		@Override
		public VariableElem resolveLocalVariable(EltSimpleName name) {
			VariableElem variableElem = variables.get(name);
			if (variableElem == null && superClass != null && superClass instanceof DeclaredTpe) {
				variableElem = ((DeclaredTpe) superClass).asElement().scope().resolveLocalVariable(name);
			}
			if (variableElem == null && interfaces != null && !interfaces.isEmpty()) {
				for (TpeMirror anInterface : interfaces) {
					if (anInterface instanceof DeclaredTpe) {
						variableElem = ((DeclaredTpe) anInterface).asElement().scope().resolveLocalVariable(name);
						if (variableElem != null) break;
					}
				}
			}
			return variableElem;
		}

		@Override
		public ExecutableElem resolveLocalExecutable(EltSimpleName name) {
			ExecutableElem executableElem = executables.get(name);
			if (executableElem == null && superClass != null && superClass instanceof DeclaredTpe) {
				executableElem = ((DeclaredTpe) superClass).asElement().scope().resolveLocalExecutable(name);
			}
			if (executableElem == null && interfaces != null && !interfaces.isEmpty()) {
				for (TpeMirror anInterface : interfaces) {
					if (anInterface instanceof DeclaredTpe) {
						executableElem = ((DeclaredTpe) anInterface).asElement().scope().resolveLocalExecutable(name);
						if (executableElem != null) break;
					}
				}
			}
			return executableElem;
		}
	};
}
