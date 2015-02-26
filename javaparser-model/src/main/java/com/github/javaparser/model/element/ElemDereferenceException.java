package com.github.javaparser.model.element;

/**
 * @author Didier Villevalois
 */
public class ElemDereferenceException extends RuntimeException {

	public ElemDereferenceException(String message) {
		super(message);
	}

	public ElemDereferenceException(String message, Throwable cause) {
		super(message, cause);
	}
}
