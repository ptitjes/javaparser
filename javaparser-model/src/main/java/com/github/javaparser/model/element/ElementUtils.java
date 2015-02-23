package com.github.javaparser.model.element;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.scope.EltNames;

import javax.lang.model.element.*;
import javax.lang.model.util.Elements;
import java.io.Writer;
import java.util.List;
import java.util.Map;

/**
 * @author Didier Villevalois
 */
public class ElementUtils implements Elements, Registry.Participant {

	private Classpath classpath;

	@Override
	public void configure(Registry registry) {
		classpath = registry.get(Classpath.class);
	}

	/* Internal convenience API */

	public TypeElem java_lang_Object() {
		return (TypeElem) getTypeElement("java.lang.Object");
	}

	public TypeElem java_lang_Enum() {
		return (TypeElem) getTypeElement("java.lang.Enum");
	}

	public TypeElem java_lang_annotation_Annotation() {
		return (TypeElem) getTypeElement("java.lang.annotation.Annotation");
	}

	/* Standard API */

	@Override
	public PackageElement getPackageElement(CharSequence name) {
		// TODO We should have a unique PackageElement per package...
		return classpath.getSourcePackage(EltNames.make(name));
	}

	@Override
	public TypeElement getTypeElement(CharSequence name) {
		return classpath.sourceScope().resolveType(EltNames.make(name));
	}

	@Override
	public Map<? extends ExecutableElement, ? extends AnnotationValue> getElementValuesWithDefaults(AnnotationMirror a) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public String getDocComment(Element e) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean isDeprecated(Element e) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public Name getBinaryName(TypeElement type) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public PackageElement getPackageOf(Element type) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public List<? extends Element> getAllMembers(TypeElement type) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public List<? extends AnnotationMirror> getAllAnnotationMirrors(Element e) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean hides(Element hider, Element hidden) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean overrides(ExecutableElement overrider, ExecutableElement overridden, TypeElement type) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public String getConstantExpression(Object value) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public void printElements(Writer w, Element... elements) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public Name getName(CharSequence cs) {
		throw new UnsupportedOperationException("To be implemented!");
	}

	@Override
	public boolean isFunctionalInterface(TypeElement type) {
		throw new UnsupportedOperationException("To be implemented!");
	}
}
