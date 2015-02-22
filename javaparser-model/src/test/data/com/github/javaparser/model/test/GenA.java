package com.github.javaparser.model.test;

/**
 * @author Didier Villevalois
 */
interface InterfaceA {
}

class GenA<X/* extends Y*/, Y extends X & InterfaceA> extends GenParent<X, Y> {
}

class GenParent<T, U> {
}
