package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.PackageDeclaration;
import com.github.javaparser.ast.TypeParameter;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.stmt.*;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.classpath.ClasspathElement;
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

	public void process(ClasspathElement file, CompilationUnit cu) {
		PackageDeclaration packageDecl = cu.getPackage();
		EltName packageName = packageDecl == null ? EltNames.empty : asName(packageDecl.getName());

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

		private void recurse(Node n, Elem elem, Context arg) {
			Context inside = arg.inside(elem);
			visitAll(this, inside, n.getChildrenNodes());
		}

		/* Declarations */

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Context arg) {
			visitTypeDeclaration(n, n.isInterface() ? ElementKind.INTERFACE : ElementKind.CLASS, arg);
		}

		@Override
		public void visit(AnnotationDeclaration n, Context arg) {
			visitTypeDeclaration(n, ElementKind.ANNOTATION_TYPE, arg);
		}

		@Override
		public void visit(EnumDeclaration n, Context arg) {
			visitTypeDeclaration(n, ElementKind.ENUM, arg);
		}

		private void visitTypeDeclaration(TypeDeclaration n, ElementKind kind, Context arg) {
			String name = n.getName();
			TypeElem elem = new TypeElem(arg.originFor(n), arg.scope, arg.elem,
					convert(n.getModifiers()),
					arg.qualifiedName(name),
					EltNames.makeSimple(name),
					kind, arg.nestingKind());
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(ObjectCreationExpr n, Context arg) {
			TypeElem elem = new TypeElem(arg.originFor(n), arg.scope, arg.elem,
					EnumSet.noneOf(Modifier.class),
					EltNames.empty,
					EltNames.empty,
					ElementKind.CLASS, arg.nestingKind());
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(TypeParameter n, Context arg) {
			TypeParameterElem elem = new TypeParameterElem(arg.originFor(n), arg.elem,
					EltNames.makeSimple(n.getName()));
			arg.setElemAttributes(n, elem);
		}

		@Override
		public void visit(InitializerDeclaration n, Context arg) {
			ExecutableElem elem = new ExecutableElem(arg.originFor(n), arg.elem,
					n.isStatic() ? EnumSet.of(Modifier.STATIC) : EnumSet.noneOf(Modifier.class),
					EltNames.empty,
					n.isStatic() ? ElementKind.STATIC_INIT : ElementKind.INSTANCE_INIT);
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(ConstructorDeclaration n, Context arg) {
			ExecutableElem elem = new ExecutableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.CONSTRUCTOR);
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Context arg) {
			ExecutableElem elem = new ExecutableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.METHOD);
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(Parameter n, Context arg) {
			VariableElem elem = new VariableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getId().getName()),
					ElementKind.PARAMETER);
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(MultiTypeParameter n, Context arg) {
			VariableElem elem = new VariableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getId().getName()),
					ElementKind.EXCEPTION_PARAMETER);
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(AnnotationMemberDeclaration n, Context arg) {
			ExecutableElem elem = new ExecutableElem(arg.originFor(n), arg.elem,
					convert(n.getModifiers()),
					EltNames.makeSimple(n.getName()),
					ElementKind.METHOD);
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Context arg) {
			Set<Modifier> modifiers = convert(n.getModifiers());

			for (VariableDeclarator d : n.getVariables()) {
				VariableElem elem = new VariableElem(arg.originFor(n), arg.elem,
						modifiers,
						EltNames.makeSimple(d.getId().getName()),
						ElementKind.FIELD);
				arg.setElemAttributes(n, elem);

				recurse(n, elem, arg);
			}
		}

		@Override
		public void visit(EnumConstantDeclaration n, Context arg) {
			VariableElem elem = new VariableElem(arg.originFor(n), arg.elem,
					EnumSet.of(Modifier.PUBLIC),
					EltNames.makeSimple(n.getName()),
					ElementKind.ENUM_CONSTANT);
			arg.setElemAttributes(n, elem);

			recurse(n, elem, arg);
		}

		/* Statements */

		@Override
		public void visit(BlockStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(BreakStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ContinueStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ForStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ForeachStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ExpressionStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(EmptyStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(DoStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(AssertStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ExplicitConstructorInvocationStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(IfStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(LabeledStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ReturnStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(SwitchEntryStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(SwitchStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(SynchronizedStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ThrowStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(TryStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(CatchClause n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(TypeDeclarationStmt n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(WhileStmt n, Context arg) {
			super.visit(n, arg);
		}

		/* Expressions */

		@Override
		public void visit(ArrayAccessExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ArrayCreationExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ArrayInitializerExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(AssignExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(BinaryExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(BooleanLiteralExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(CastExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(CharLiteralExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ClassExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ConditionalExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(DoubleLiteralExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(EnclosedExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(FieldAccessExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(InstanceOfExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(IntegerLiteralExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(IntegerLiteralMinValueExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(LongLiteralExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(LongLiteralMinValueExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodCallExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(NullLiteralExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(StringLiteralExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(SuperExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(ThisExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(UnaryExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(VariableDeclarationExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(LambdaExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(MethodReferenceExpr n, Context arg) {
			super.visit(n, arg);
		}

		@Override
		public void visit(TypeExpr n, Context arg) {
			super.visit(n, arg);
		}
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
					return EltNames.empty;
			}
		}
	}
}
