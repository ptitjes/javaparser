package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.element.*;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltNames;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.source.CompilationUnitAttr;
import com.github.javaparser.model.source.ElementAttr;
import com.github.javaparser.model.source.SourceOrigin;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import java.io.File;
import java.util.EnumSet;
import java.util.Set;

import static com.github.javaparser.model.source.utils.ModifiersUtils.convert;
import static com.github.javaparser.model.source.utils.NodeListUtils.visitAll;
import static com.github.javaparser.model.source.utils.SrcNameUtils.asName;

/**
 * @author Didier Villevalois
 */
public class Scaffolding {

	private final Analysis analysis;

	public Scaffolding(Analysis analysis) {
		this.analysis = analysis;
	}

	public void process(File file, CompilationUnit cu) {
		PackageDeclaration packageDecl = cu.getPackage();
		EltName packageName = asName(packageDecl.getName());

		PackageElem packageElem = analysis.getSourcePackage(packageName);
		if (packageElem == null) {
			SourceOrigin origin = new SourceOrigin(cu, packageDecl);
			packageElem = new PackageElem(analysis.sourceScope(), origin, packageName);
			analysis.addSourcePackage(packageElem);
		}

		CompilationUnitAttr attr = new CompilationUnitAttr(packageElem.scope(), file, cu);

		visitAll(new ElemBuilder(cu, attr.scope()), packageElem, cu.getTypes());
	}

	class ElemBuilder extends VoidVisitorAdapter<Elem> {

		private final CompilationUnit cu;
		private final Scope scope;

		public ElemBuilder(CompilationUnit cu, Scope scope) {
			this.cu = cu;
			this.scope = scope;
		}

		private SourceOrigin originFor(Node n) {
			return new SourceOrigin(cu, n);
		}

		private NestingKind nestingKind(Elem parent) {
			return parent instanceof PackageElem ? NestingKind.TOP_LEVEL : NestingKind.MEMBER;
		}

		private Scope scopeFor(Elem parent) {
			return parent instanceof PackageElem ? scope : parent.scope();
		}

		private EltName makeQualifiedName(Elem parent, String name) {
			if (name.isEmpty() || parent.getSimpleName().isEmpty())
				return EltNames.makeSimple(name);
			else if (parent instanceof PackageElem)
				return EltNames.make(((PackageElem) parent).getQualifiedName(), name);
			else if (parent instanceof TypeElem)
				return EltNames.make(((TypeElem) parent).getQualifiedName(), name);
			else return EltNames.makeSimple(name);
		}

		private <E extends Elem> void setAttributes(Node n, E elem) {
			new ElementAttr<E>(cu, n, elem);
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Elem arg) {
			String name = n.getName();
			TypeElem elem = new TypeElem(originFor(n), scopeFor(arg), arg,
					convert(n.getModifiers()),
					makeQualifiedName(arg, name),
					EltNames.makeSimple(name),
					n.isInterface() ? ElementKind.INTERFACE : ElementKind.CLASS,
					nestingKind(arg));
			setAttributes(n, elem);

			visitAll(this, elem, n.getTypeParameters());
			visitAll(this, elem, n.getMembers());
		}

		@Override
		public void visit(AnnotationDeclaration n, Elem arg) {
			String name = n.getName();
			TypeElem elem = new TypeElem(originFor(n), scopeFor(arg), arg,
					convert(n.getModifiers()),
					makeQualifiedName(arg, name),
					EltNames.makeSimple(name),
					ElementKind.ANNOTATION_TYPE,
					nestingKind(arg));
			setAttributes(n, elem);

			visitAll(this, elem, n.getMembers());
		}

		@Override
		public void visit(EnumDeclaration n, Elem arg) {
			String name = n.getName();
			TypeElem elem = new TypeElem(originFor(n), scopeFor(arg), arg,
					convert(n.getModifiers()),
					makeQualifiedName(arg, name),
					EltNames.makeSimple(name),
					ElementKind.ENUM,
					nestingKind(arg));
			setAttributes(n, elem);

			visitAll(this, elem, n.getEntries());
			visitAll(this, elem, n.getMembers());
		}

		@Override
		public void visit(TypeParameter n, Elem arg) {
			TypeParameterElem elem = new TypeParameterElem(originFor(n), arg, EltNames.makeSimple(n.getName()));
			setAttributes(n, elem);
		}

		@Override
		public void visit(ConstructorDeclaration n, Elem arg) {
			ExecutableElem elem = new ExecutableElem(originFor(n), arg,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.CONSTRUCTOR);
			setAttributes(n, elem);

			visitAll(this, elem, n.getParameters());
		}

		@Override
		public void visit(MethodDeclaration n, Elem arg) {
			ExecutableElem elem = new ExecutableElem(originFor(n), arg,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.METHOD);
			setAttributes(n, elem);

			visitAll(this, elem, n.getParameters());
		}

		@Override
		public void visit(AnnotationMemberDeclaration n, Elem arg) {
			setAttributes(n, new ExecutableElem(originFor(n), arg,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.METHOD));
		}

		@Override
		public void visit(FieldDeclaration n, Elem arg) {
			Set<Modifier> modifiers = convert(n.getModifiers());

			for (VariableDeclarator d : n.getVariables()) {
				setAttributes(n, new VariableElem(originFor(n), arg,
						modifiers,
						EltNames.makeSimple(d.getId().getName()),
						ElementKind.FIELD));
			}
		}

		@Override
		public void visit(EnumConstantDeclaration n, Elem arg) {
			setAttributes(n, new VariableElem(originFor(n), arg,
					EnumSet.of(Modifier.PUBLIC),
					EltNames.makeSimple(n.getName()),
					ElementKind.ENUM_CONSTANT));
		}
	}
}
