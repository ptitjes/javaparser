package com.github.javaparser.model.type;

import javax.lang.model.element.AnnotationMirror;
import javax.lang.model.type.TypeMirror;
import java.lang.annotation.Annotation;
import java.util.List;

/**
 * @author Didier Villevalois
 */
public abstract class TpeMirror implements TypeMirror {

	@Override
	public List<? extends AnnotationMirror> getAnnotationMirrors() {
		return null;
	}

	@Override
	public <A extends Annotation> A getAnnotation(Class<A> annotationType) {
		return null;
	}

	@Override
	public <A extends Annotation> A[] getAnnotationsByType(Class<A> annotationType) {
		return null;
	}

	protected static String allToString(List<? extends TpeMirror> types, String separator) {
		StringBuffer buffer = new StringBuffer();
		boolean first = true;
		for (TpeMirror type : types) {
			if (!first) buffer.append(separator);
			else first = false;
			buffer.append(type.toString());
		}
		return buffer.toString();
	}

	protected static String allToString(List<? extends TpeMirror> types) {
		return allToString(types, ",");
	}
}
