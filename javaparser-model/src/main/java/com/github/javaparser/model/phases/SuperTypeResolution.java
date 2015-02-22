package com.github.javaparser.model.phases;

import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.EnumDeclaration;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.visitor.VoidVisitorAdapter;
import com.github.javaparser.model.Analysis;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.report.Reporter;
import com.github.javaparser.model.report.Reporter.Severity;
import com.github.javaparser.model.scope.ScopeException;
import com.github.javaparser.model.source.SourceOrigin;
import com.github.javaparser.model.type.NoTpe;
import com.github.javaparser.model.type.TpeMirror;

import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.ElementScanner8;
import java.util.ArrayList;
import java.util.List;

import static com.github.javaparser.model.source.utils.SrcNameUtils.asName;

/**
 * @author Didier Villevalois
 */
public class SuperTypeResolution {

	private final Analysis analysis;

	public SuperTypeResolution(Analysis analysis) {
		this.analysis = analysis;
	}

	public void process() {
		for (PackageElement packageElement : analysis.getPackageElements()) {
			scanner.scan(packageElement);
		}
	}

	private void process(TypeElem e, ClassOrInterfaceDeclaration n) {
		List<ClassOrInterfaceType> extended = n.getExtends();
		List<ClassOrInterfaceType> implemented = n.getImplements();

		if (n.isInterface()) {
			e.setSuperClass(NoTpe.NONE);

			try {
				List<TpeMirror> interfaces = new ArrayList<TpeMirror>();
				if (implemented != null) {
					for (ClassOrInterfaceType type : extended) {
						TypeElem typeElem = e.parentScope().resolveType(asName(type));
						interfaces.add(typeElem.asType());
					}
				}
				e.setInterfaces(interfaces);
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
			}
		} else {
			try {
				if (extended != null && extended.size() == 1) {
					ClassOrInterfaceType type = extended.get(0);
					TypeElem typeElem = e.parentScope().resolveType(asName(type));
					e.setSuperClass(typeElem.asType());
				} else e.setSuperClass(NoTpe.NONE);
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
			}

			try {
				List<TpeMirror> interfaces = new ArrayList<TpeMirror>();
				if (implemented != null) {
					for (ClassOrInterfaceType type : implemented) {
						TypeElem typeElem = e.parentScope().resolveType(asName(type));
						interfaces.add(typeElem.asType());
					}
				}
				e.setInterfaces(interfaces);
			} catch (ScopeException ex) {
				analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
			}
		}
	}

	private void process(TypeElem e, EnumDeclaration n) {
		List<ClassOrInterfaceType> implemented = n.getImplements();

		try {
			List<TpeMirror> interfaces = new ArrayList<TpeMirror>();
			if (implemented != null) {
				for (ClassOrInterfaceType type : implemented) {
					TypeElem typeElem = e.parentScope().resolveType(asName(type));
					interfaces.add(typeElem.asType());
				}
			}
			e.setInterfaces(interfaces);
		} catch (ScopeException ex) {
			analysis.report(Severity.ERROR, "Can't resolve supertype: " + ex.getMessage(), e.origin());
		}
	}

	private ElementScanner8<Void, Void> scanner = new ElementScanner8<Void, Void>() {

		@Override
		public Void visitType(TypeElement e, Void aVoid) {
			TypeElem typeElem = (TypeElem) e;
			SourceOrigin origin = (SourceOrigin) typeElem.origin();
			origin.getNode().accept(discriminator, typeElem);

			return super.visitType(e, aVoid);
		}
	};

	private VoidVisitorAdapter<TypeElem> discriminator = new VoidVisitorAdapter<TypeElem>() {

		@Override
		public void visit(ClassOrInterfaceDeclaration n, TypeElem arg) {
			process(arg, n);
		}

		@Override
		public void visit(EnumDeclaration n, TypeElem arg) {
			process(arg, n);
		}
	};
}
