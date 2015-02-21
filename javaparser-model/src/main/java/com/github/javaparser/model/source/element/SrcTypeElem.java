package com.github.javaparser.model.source.element;

import com.github.javaparser.ast.Node;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.type.DeclaredTpe;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import java.util.*;

/**
 * @author Didier Villevalois
 */
public class SrcTypeElem extends SrcElem implements TypeElement {

	private final EltName qualifiedName;
	private final NestingKind nesting;
	private final List<SrcTypeParameterElem> typeParameters = new ArrayList<SrcTypeParameterElem>();
	private final Map<EltName, SrcTypeElem> types = new HashMap<EltName, SrcTypeElem>();
	private final Map<EltName, SrcExecutableElem> executables = new HashMap<EltName, SrcExecutableElem>();
	private final Map<EltName, SrcVariableElem> variables = new HashMap<EltName, SrcVariableElem>();

	public SrcTypeElem(SrcFile source,
	                   Node node,
	                   SrcElem enclosing,
	                   Set<Modifier> modifiers,
	                   EltName qualifiedName,
	                   EltName simpleName,
	                   ElementKind kind,
	                   NestingKind nesting) {
		super(source, node, enclosing, modifiers, simpleName, kind);
		this.qualifiedName = qualifiedName;
		this.nesting = nesting;
	}

	@Override
	protected void addEnclosedElem(SrcElem elem) {
		super.addEnclosedElem(elem);

		EltName name = elem.getSimpleName();
		if (name.isEmpty()) return;

		switch (elem.getKind()) {
			case TYPE_PARAMETER:
				typeParameters.add((SrcTypeParameterElem) elem);
				break;
			case CLASS:
			case INTERFACE:
			case ANNOTATION_TYPE:
			case ENUM:
				types.put(name, (SrcTypeElem) elem);
				break;
			case CONSTRUCTOR:
			case METHOD:
			case STATIC_INIT:
			case INSTANCE_INIT:
				executables.put(name, (SrcExecutableElem) elem);
				break;
			case FIELD:
			case ENUM_CONSTANT:
				variables.put(name, (SrcVariableElem) elem);
				break;
		}
	}

	@Override
	public Name getQualifiedName() {
		return qualifiedName;
	}

	@Override
	public NestingKind getNestingKind() {
		return nesting;
	}

	@Override
	public List<? extends TypeParameterElement> getTypeParameters() {
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
		for (SrcTypeParameterElem typeParameter : typeParameters) {
			typeParametersAsTypes.add(typeParameter.asType());
		}
		return new DeclaredTpe(getEnclosingElement().asType(), this, typeParametersAsTypes);
	}

	private final Scope scope = new Scope(getEnclosingElement().definedScope()) {

		@Override
		public TypeElement resolveLocalType(EltName name) {
			return types.get(name);
		}

		@Override
		public VariableElement resolveLocalVariable(EltName name) {
			return variables.get(name);
		}

		@Override
		public ExecutableElement resolveLocalExecutable(EltName name) {
			return executables.get(name);
		}
	};
}
