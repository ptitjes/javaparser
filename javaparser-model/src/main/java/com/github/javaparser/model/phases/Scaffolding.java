package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltNames;
import com.github.javaparser.model.source.base.SrcFile;
import com.github.javaparser.model.source.element.*;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;

import java.util.EnumSet;
import java.util.Set;

import static com.github.javaparser.model.source.utils.ModifiersUtils.convert;
import static com.github.javaparser.model.source.utils.NodeListUtils.visitAll;
import static com.github.javaparser.model.source.utils.SrcNameUtils.asName;

/**
 * @author Didier Villevalois
 */
public class Scaffolding {

	public void scaffold(Analysis analysis) {
		for (CompilationUnit cu : analysis.getCompilationUnits()) {
			PackageDeclaration packageDecl = cu.getPackage();
			EltName packageName = asName(packageDecl.getName());
			SrcPackageElem packageElem = analysis.getSourcePackage(packageName);

			SrcFile source = new SrcFile(analysis, packageElem, cu);
			visitAll(new ElemBuilder(source), packageElem, cu.getTypes());
		}
	}

	class ElemBuilder extends VoidVisitorAdapter<SrcElem> {

		private final SrcFile source;

		public ElemBuilder(SrcFile source) {
			this.source = source;
		}

		@Override
		public void visit(ClassOrInterfaceDeclaration n, SrcElem arg) {
			SrcTypeElem elem = new SrcTypeElem(source, n, arg,
					convert(n.getModifiers()),
					asName(n.getNameExpr()), // Not good - should be pkg name + name or empty name
					EltNames.make(n.getName()),
					n.isInterface() ? ElementKind.INTERFACE : ElementKind.CLASS,
					arg instanceof SrcPackageElem ? NestingKind.TOP_LEVEL : NestingKind.MEMBER);
			visitAll(this, elem, n.getTypeParameters());
			visitAll(this, elem, n.getMembers());
		}

		@Override
		public void visit(AnnotationDeclaration n, SrcElem arg) {
			SrcTypeElem elem = new SrcTypeElem(source, n, arg,
					convert(n.getModifiers()),
					asName(n.getNameExpr()), // Not good - should be pkg name + name or empty name
					EltNames.make(n.getName()),
					ElementKind.ANNOTATION_TYPE,
					arg instanceof SrcPackageElem ? NestingKind.TOP_LEVEL : NestingKind.MEMBER);
			visitAll(this, elem, n.getMembers());
		}

		@Override
		public void visit(EnumDeclaration n, SrcElem arg) {
			SrcTypeElem elem = new SrcTypeElem(source, n, arg,
					convert(n.getModifiers()),
					asName(n.getNameExpr()), // Not good - should be pkg name + name or empty name
					EltNames.make(n.getName()),
					ElementKind.ENUM,
					arg instanceof SrcPackageElem ? NestingKind.TOP_LEVEL : NestingKind.MEMBER);
			visitAll(this, elem, n.getEntries());
			visitAll(this, elem, n.getMembers());
		}

		@Override
		public void visit(TypeParameter n, SrcElem arg) {
			new SrcTypeParameterElem(source, n, arg, EltNames.make(n.getName()));
		}

		@Override
		public void visit(ConstructorDeclaration n, SrcElem arg) {
			SrcExecutableElem elem = new SrcExecutableElem(source, n, arg,
					convert(n.getModifiers()),
					EltNames.make(n.getName()),
					ElementKind.CONSTRUCTOR);
			visitAll(this, elem, n.getParameters());
		}

		@Override
		public void visit(MethodDeclaration n, SrcElem arg) {
			SrcExecutableElem elem = new SrcExecutableElem(source, n, arg,
					convert(n.getModifiers()),
					EltNames.make(n.getName()),
					ElementKind.METHOD);
			visitAll(this, elem, n.getParameters());
		}

		@Override
		public void visit(AnnotationMemberDeclaration n, SrcElem arg) {
			new SrcExecutableElem(source, n, arg,
					convert(n.getModifiers()),
					EltNames.make(n.getName()),
					ElementKind.METHOD);
		}

		@Override
		public void visit(FieldDeclaration n, SrcElem arg) {
			Set<Modifier> modifiers = convert(n.getModifiers());

			for (VariableDeclarator d : n.getVariables()) {
				new SrcVariableElem(source, n, arg,
						modifiers,
						EltNames.make(d.getId().getName()),
						ElementKind.FIELD);
			}
		}

		@Override
		public void visit(EnumConstantDeclaration n, SrcElem arg) {
			new SrcVariableElem(source, n, arg,
					EnumSet.of(Modifier.PUBLIC),
					EltNames.make(n.getName()),
					ElementKind.ENUM_CONSTANT);
		}
	}
}
