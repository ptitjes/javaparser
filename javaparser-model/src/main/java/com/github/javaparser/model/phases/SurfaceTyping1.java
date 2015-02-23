package com.github.javaparser.model.phases;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.body.AnnotationDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.report.Reporter.Severity;
import com.github.javaparser.model.scope.ScopeException;
import com.github.javaparser.model.source.ElementAttr;
import com.github.javaparser.model.type.DeclaredTpe;
import com.github.javaparser.model.type.NoTpe;
import com.github.javaparser.model.type.TpeMirror;
import com.github.javaparser.model.type.TypeUtils;

import java.util.Collections;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public class SurfaceTyping1 {

	private final Analysis analysis;
	private final TypeUtils typeUtils;
	private final TypeResolver typeResolver;

	public SurfaceTyping1(Analysis analysis) {
		this.analysis = analysis;

		typeUtils = analysis.getTypeUtils();
		typeResolver = new TypeResolver(analysis);
	}

	public void process() {
		for (CompilationUnit cu : analysis.getCompilationUnits()) {
			cu.accept(scanner, null);
		}
	}

	private VoidVisitorAdapter<Void> scanner = new VoidVisitorAdapter<Void>() {

		@Override
		public void visit(ClassOrInterfaceDeclaration n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			try {
				typeResolver.resolveTypeParameters(elem.getTypeParameters(), elem.scope());
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, ex.getMessage(), elem.origin());
			}

			List<ClassOrInterfaceType> extended = n.getExtends();
			List<ClassOrInterfaceType> implemented = n.getImplements();

			if (n.isInterface()) {
				elem.setSuperClass(NoTpe.NONE);

				try {
					elem.setInterfaces(typeResolver.resolveTypes(extended, elem.scope()));
				} catch (ScopeException ex) {
					analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), elem.origin());
				}
			} else {
				if (extended != null && extended.size() == 1) {
					try {
						ClassOrInterfaceType type = extended.get(0);
						elem.setSuperClass(typeResolver.resolveType(type, elem.scope()));
					} catch (ScopeException ex) {
						analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), elem.origin());
					}
				} else elem.setSuperClass(NoTpe.NONE);

				try {
					elem.setInterfaces(typeResolver.resolveTypes(implemented, elem.scope()));
				} catch (ScopeException ex) {
					analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), elem.origin());
				}
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(EnumDeclaration n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			List<ClassOrInterfaceType> implemented = n.getImplements();

			elem.setSuperClass(typeUtils.enumTypeOf(elem.asType()));

			try {
				elem.setInterfaces(typeResolver.resolveTypes(implemented, elem.scope()));
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), elem.origin());
			}

			super.visit(n, arg);
		}

		@Override
		public void visit(AnnotationDeclaration n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			elem.setSuperClass(NoTpe.NONE);
			elem.setInterfaces(Collections.<TpeMirror>singletonList(typeUtils.annotationType()));

			super.visit(n, arg);
		}

		@Override
		public void visit(ObjectCreationExpr n, Void arg) {
			ElementAttr<TypeElem> attr = ElementAttr.get(n);
			TypeElem elem = attr.element();

			ClassOrInterfaceType type = n.getType();

			try {
				DeclaredTpe tpeMirror = (DeclaredTpe) typeResolver.resolveType(type, elem.scope());
				switch (tpeMirror.asElement().getKind()) {
					case INTERFACE:
						elem.setSuperClass(typeUtils.objectType());
						elem.setInterfaces(Collections.<TpeMirror>singletonList(tpeMirror));
						break;
					case CLASS:
						elem.setSuperClass(tpeMirror);
						elem.setInterfaces(Collections.<TpeMirror>emptyList());
						break;
					default:
						throw new IllegalStateException();
				}
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), elem.origin());
			}

			super.visit(n, arg);
		}
	};
}
