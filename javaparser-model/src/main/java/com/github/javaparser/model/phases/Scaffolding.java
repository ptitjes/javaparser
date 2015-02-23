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
		visitAll(attributesBuilder, new Context(cu, attr.scope(), packageElem), cu.getTypes());
	}

	VoidVisitorAdapter<Context> attributesBuilder = new VoidVisitorAdapter<Context>() {

		/* Declarations */

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Context arg) {
			String name = n.getName();
			TypeElem elem = new TypeElem(arg.originFor(n), arg.scope, arg.elem,
					convert(n.getModifiers()),
					arg.qualifiedName(name),
					EltNames.makeSimple(name),
					n.isInterface() ? ElementKind.INTERFACE : ElementKind.CLASS,
					arg.nestingKind());
			arg.setElemAttributes(n, elem);

			Context inside = arg.inside(elem);
			visitAll(this, inside, n.getTypeParameters());
			visitAll(this, inside, n.getMembers());
		}

		@Override
		public void visit(AnnotationDeclaration n, Context arg) {
			String name = n.getName();
			TypeElem elem = new TypeElem(arg.originFor(n), arg.scope, arg.elem,
					convert(n.getModifiers()),
					arg.qualifiedName(name),
					EltNames.makeSimple(name),
					ElementKind.ANNOTATION_TYPE,
					arg.nestingKind());
			arg.setElemAttributes(n, elem);

			Context inside = arg.inside(elem);
			visitAll(this, inside, n.getMembers());
		}

		@Override
		public void visit(EnumDeclaration n, Context arg) {
			String name = n.getName();
			TypeElem elem = new TypeElem(arg.originFor(n), arg.scope, arg.elem,
					convert(n.getModifiers()),
					arg.qualifiedName(name),
					EltNames.makeSimple(name),
					ElementKind.ENUM,
					arg.nestingKind());
			arg.setElemAttributes(n, elem);

			Context inside = arg.inside(elem);
			visitAll(this, inside, n.getEntries());
			visitAll(this, inside, n.getMembers());
		}

		@Override
		public void visit(TypeParameter n, Context arg) {
			TypeParameterElem elem = new TypeParameterElem(arg.originFor(n), arg.elem,
					EltNames.makeSimple(n.getName()));
			arg.setElemAttributes(n, elem);
		}

		@Override
		public void visit(ConstructorDeclaration n, Context arg) {
			ExecutableElem elem = new ExecutableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.CONSTRUCTOR);
			arg.setElemAttributes(n, elem);

			Context inside = arg.inside(elem);
			visitAll(this, inside, n.getParameters());
		}

		@Override
		public void visit(MethodDeclaration n, Context arg) {
			ExecutableElem elem = new ExecutableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.METHOD);
			arg.setElemAttributes(n, elem);

			Context inside = arg.inside(elem);
			visitAll(this, inside, n.getParameters());
		}

		@Override
		public void visit(AnnotationMemberDeclaration n, Context arg) {
			arg.setElemAttributes(n, new ExecutableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.METHOD));
		}

		@Override
		public void visit(FieldDeclaration n, Context arg) {
			Set<Modifier> modifiers = convert(n.getModifiers());

			for (VariableDeclarator d : n.getVariables()) {
				arg.setElemAttributes(n, new VariableElem(arg.originFor(n), arg.elem,
						modifiers,
						EltNames.makeSimple(d.getId().getName()),
						ElementKind.FIELD));
			}
		}

		@Override
		public void visit(EnumConstantDeclaration n, Context arg) {
			arg.setElemAttributes(n, new VariableElem(arg.originFor(n), arg.elem,
					EnumSet.of(Modifier.PUBLIC),
					EltNames.makeSimple(n.getName()),
					ElementKind.ENUM_CONSTANT));
		}

		/* Statements */

		/* Expressions */

	};

	class Context {

		public final CompilationUnit cu;
		public final Scope scope;
		public final Elem elem;

		public Context(CompilationUnit cu, Scope scope, Elem elem) {
			this.cu = cu;
			this.scope = scope;
			this.elem = elem;
		}

		public Context inside(Elem elem) {
			return new Context(cu, elem.scope(), elem);
		}

		public Context inside(Scope scope) {
			return new Context(cu, scope, elem);
		}

		public <E extends Elem> void setElemAttributes(Node n, E elem) {
			new ElementAttr<E>(cu, n, elem);
		}

		public SourceOrigin originFor(Node n) {
			return new SourceOrigin(cu, n);
		}

		public NestingKind nestingKind() {
			switch (elem.getKind()) {
				case PACKAGE:
					return NestingKind.TOP_LEVEL;
				case ENUM:
				case CLASS:
				case ANNOTATION_TYPE:
				case INTERFACE:
					return NestingKind.MEMBER;
				case ENUM_CONSTANT:
				case FIELD:
				case LOCAL_VARIABLE:
				case RESOURCE_VARIABLE: // TODO What to do about that ???
					return NestingKind.ANONYMOUS;
				case METHOD:
				case CONSTRUCTOR:
				case STATIC_INIT:
				case INSTANCE_INIT:
					return NestingKind.LOCAL;
				default:
					throw new IllegalStateException();
			}
		}

		public EltName qualifiedName(String name) {
			if (name.isEmpty() || elem.getSimpleName().isEmpty())
				return EltNames.makeSimple(name);
			else switch (elem.getKind()) {
				case PACKAGE:
					return EltNames.make(((PackageElem) elem).getQualifiedName(), name);
				case ENUM:
				case CLASS:
				case ANNOTATION_TYPE:
				case INTERFACE:
					return EltNames.make(((TypeElem) elem).getQualifiedName(), name);
				default:
					return EltNames.makeSimple(name);
			}
		}
	}
}
