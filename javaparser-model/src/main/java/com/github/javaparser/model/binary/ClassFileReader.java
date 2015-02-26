package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.classpath.Classpath;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.scope.Scope;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;

import java.io.IOException;
import java.io.InputStream;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Didier Villevalois
 */
public class ClassFileReader implements Registry.Participant {

	private BinaryTypeBuilder typeBuilder;

	@Override
	public void configure(Registry registry) {
		typeBuilder = registry.get(BinaryTypeBuilder.class);
	}

	/**
	 * Reads a package from the input stream of a 'package.java' class file.
	 *
	 * @param scope       the scope this package is defined
	 * @param inputStream the class file input stream
	 * @return the read package element
	 */
	public PackageElem readPackage(Scope scope,
	                               InputStream inputStream) throws IOException {
		ClassReader reader = new ClassReader(inputStream);
		// ...
		return null;
	}

	/**
	 * Reads a type from the input stream of a class file.
	 *
	 * @param scope       the scope this type is defined
	 * @param enclosing   the enclosing element
	 * @param inputStream the class file input stream
	 * @return the read type element
	 */
	public TypeElem readClass(Scope scope,
	                          Elem enclosing,
	                          InputStream inputStream) throws IOException {
		ClassReader reader = new ClassReader(inputStream);
		TypeElemBuilder builder = new TypeElemBuilder(scope, enclosing);
		reader.accept(builder, ClassReader.SKIP_CODE);
		return builder.getTypeElem();
	}

	class TypeElemBuilder extends ClassVisitor {

		public TypeElemBuilder(Scope scope, Elem enclosing) {
			super(ASM5);
		}

		public TypeElem getTypeElem() {
			return null;
		}

		// ...
	}
}
