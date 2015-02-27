package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.*;
import com.github.javaparser.ast.expr.*;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.GenericVisitorAdapter;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.element.ExecutableElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.VariableElem;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.scope.ScopeException;
import com.github.javaparser.model.source.ElementAttr;
import com.github.javaparser.model.type.NoTpe;
import com.github.javaparser.model.type.TpeMirror;
import com.github.javaparser.model.type.TypeUtils;

import javax.lang.model.element.AnnotationValue;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.github.javaparser.model.source.utils.CharUtils.unEscapeChar;
import static com.github.javaparser.model.source.utils.CharUtils.unEscapeString;

/**
 * @author Didier Villevalois
 */
public class SurfaceTyping2 implements Registry.Participant {

	private Reporter reporter;
	private Classpath classpath;
	private TypeUtils typeUtils;
	private TypeResolver typeResolver;

	@Override
	public void configure(Registry registry) {
		reporter = registry.get(Reporter.class);
		classpath = registry.get(Classpath.class);
		typeUtils = registry.get(TypeUtils.class);
		typeResolver = registry.get(TypeResolver.class);
	}

	public void process() {
		for (CompilationUnit cu : classpath.getCompilationUnits()) {
			cu.accept(scanner, null);
		}
	}

	private VoidVisitorAdapter<Void> scanner = new VoidVisitorAdapter<Void>() {

		@Override
		public void visit(InitializerDeclaration n, Void arg) {
			ElementAttr<ExecutableElem> attr = ElementAttr.get(n);
			ExecutableElem elem = attr.element();

			elem.setReceiverType(NoTpe.VOID);

			elem.setReturnType(NoTpe.VOID);

			elem.setThrownTypes(Collections.<TpeMirror>emptyList());

			elem.setVarArgs(false);

			elem.setDefaultValue(null);

			super.visit(n, arg);
		}

		@Override
		public void visit(ConstructorDeclaration n, Void arg) {
			ElementAttr<ExecutableElem> attr = ElementAttr.get(n);
			ExecutableElem elem = attr.element();

			try {
				typeResolver.resolveTypeParameters(elem.getTypeParameters(), elem.scope());
			} catch (ScopeException ex) {
				reportResolutionError(elem, ex);
			}

			TypeElem enclosingElement = (TypeElem) elem.getEnclosingElement();
			if (!enclosingElement.getNestingKind().isNested()
					// TODO This next test seems logical but not in the doc
					|| enclosingElement.getModifiers().contains(Modifier.STATIC)) {
				elem.setReceiverType(NoTpe.VOID);
			} else {
				elem.setReceiverType(enclosingElement.asType());
			}

			elem.setReturnType(NoTpe.VOID);

			try {
				elem.setThrownTypes(typeResolver.resolveTypes(namesToTypes(n.getThrows()), elem.scope()));
			} catch (ScopeException ex) {
				reportResolutionError(elem, ex);
			}

			elem.setVarArgs(hasLastParameterVarArgs(n.getParameters()));

			elem.setDefaultValue(null);

			super.visit(n, arg);
		}

		@Override
		public void visit(MethodDeclaration n, Void arg) {
			ElementAttr<ExecutableElem> attr = ElementAttr.get(n);
			ExecutableElem elem = attr.element();

			try {
				typeResolver.resolveTypeParameters(elem.getTypeParameters(), elem.scope());
			} catch (ScopeException ex) {
				reportResolutionError(elem, ex);
			}

			if (elem.getModifiers().contains(Modifier.STATIC)) {
				elem.setReceiverType(NoTpe.VOID);
			} else {
				elem.setReceiverType(elem.getEnclosingElement().asType());
			}

			try {
				elem.setThrownTypes(typeResolver.resolveTypes(namesToTypes(n.getThrows()), elem.scope()));
			} catch (ScopeException ex) {
				reportResolutionError(elem, ex);
			}

			try {
				elem.setReturnType(typeResolver.resolveType(n.getType(), elem.scope()));
			} catch (ScopeException ex) {
				reportResolutionError(elem, ex);
			}

			elem.setVarArgs(hasLastParameterVarArgs(n.getParameters()));

			elem.setDefaultValue(null);

			super.visit(n, arg);
		}

		@Override
		public void visit(AnnotationMemberDeclaration n, Void arg) {
			ElementAttr<ExecutableElem> attr = ElementAttr.get(n);
			ExecutableElem elem = attr.element();

			elem.setReceiverType(elem.getEnclosingElement().asType());

			try {
				elem.setReturnType(typeResolver.resolveType(n.getType(), elem.scope()));
			} catch (ScopeException ex) {
				reportResolutionError(elem, ex);
			}

			elem.setThrownTypes(Collections.<TpeMirror>emptyList());

			elem.setVarArgs(false);

			elem.setDefaultValue(buildDefaultValue(n.getDefaultValue()));

			super.visit(n, arg);
		}

		@Override
		public void visit(FieldDeclaration n, Void arg) {
			TpeMirror tpeMirror = null;
			boolean mayBeConstant = false;

			for (VariableDeclarator d : n.getVariables()) {
				ElementAttr<VariableElem> attr = ElementAttr.get(n);
				VariableElem elem = attr.element();

				if (tpeMirror == null) {
					try {
						tpeMirror = typeResolver.resolveType(n.getType(), elem.scope());
						mayBeConstant = elem.getModifiers().contains(Modifier.FINAL)
								&& typeUtils.isPrimitiveOrString(tpeMirror);
					} catch (ScopeException ex) {
						reporter.report(Reporter.Severity.ERROR, ex.getMessage(), elem.origin());
						break;
					}
				}

				elem.setType(tpeMirror);
				if (mayBeConstant) {
					elem.setConstantValue(buildConstantValue(d.getInit()));
				} else {
					elem.setConstantValue(null);
				}

				super.visit(d, arg);
			}
		}

		@Override
		public void visit(EnumConstantDeclaration n, Void arg) {
			ElementAttr<VariableElem> attr = ElementAttr.get(n);
			VariableElem elem = attr.element();

			elem.setType(elem.getEnclosingElement().asType());

			elem.setConstantValue(null);

			super.visit(n, arg);
		}

		@Override
		public void visit(Parameter n, Void arg) {
			ElementAttr<VariableElem> attr = ElementAttr.get(n);
			VariableElem elem = attr.element();

			try {
				elem.setType(typeResolver.resolveType(n.getType(), elem.scope()));
			} catch (ScopeException ex) {
				reporter.report(Reporter.Severity.ERROR, ex.getMessage(), elem.origin());
			}

			elem.setConstantValue(null);

			super.visit(n, arg);
		}
	};

	private void reportResolutionError(Elem elem, ScopeException ex) {
		reporter.report(Reporter.Severity.ERROR, ex.getMessage(), elem.origin());
	}

	/* This is a deviation to make do with MethodDeclaration.getThrows() returning a list of NameExpr */
	private List<? extends Type> namesToTypes(List<NameExpr> nameExprs) {
		List<Type> types = new ArrayList<Type>(nameExprs.size());
		for (NameExpr nameExpr : nameExprs) {
			types.add(asType(nameExpr));
		}
		return types;
	}

	private static ClassOrInterfaceType asType(NameExpr expr) {
		if (expr instanceof QualifiedNameExpr) {
			QualifiedNameExpr qNameExpr = (QualifiedNameExpr) expr;
			return new ClassOrInterfaceType(asType(qNameExpr.getQualifier()), qNameExpr.getName());
		} else return new ClassOrInterfaceType(null, expr.getName());
	}

	private boolean hasLastParameterVarArgs(List<Parameter> parameters) {
		return parameters.size() != 0 && parameters.get(parameters.size() - 1).isVarArgs();
	}

	private AnnotationValue buildDefaultValue(Expression defaultValue) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	private Object buildConstantValue(Expression init) {
		return init == null ? null : init.accept(constantValueBuilder, null);
	}

	private GenericVisitor<Object, Void> constantValueBuilder = new GenericVisitorAdapter<Object, Void>() {

		@Override
		public Object visit(StringLiteralExpr n, Void arg) {
			return unEscapeString(n.getValue());
		}

		@Override
		public Object visit(IntegerLiteralExpr n, Void arg) {
			return Long.parseLong(n.getValue());
		}

		@Override
		public Object visit(IntegerLiteralMinValueExpr n, Void arg) {
			return Integer.MIN_VALUE;
		}

		@Override
		public Object visit(LongLiteralExpr n, Void arg) {
			String value = n.getValue();
			return Long.parseLong(value.substring(0, value.length() - 1));
		}

		@Override
		public Object visit(LongLiteralMinValueExpr n, Void arg) {
			return Long.MIN_VALUE;
		}

		@Override
		public Object visit(NullLiteralExpr n, Void arg) {
			return null;
		}

		@Override
		public Object visit(DoubleLiteralExpr n, Void arg) {
			return Long.parseLong(n.getValue());
		}

		@Override
		public Object visit(BooleanLiteralExpr n, Void arg) {
			return n.getValue();
		}

		@Override
		public Object visit(CharLiteralExpr n, Void arg) {
			return unEscapeChar(n.getValue().toCharArray(), 0);
		}
	};
}
