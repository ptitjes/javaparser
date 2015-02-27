package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.element.Elem;
import com.github.javaparser.model.element.PackageElem;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.Scope;
import org.objectweb.asm.*;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.NestingKind;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;

import static org.objectweb.asm.Opcodes.*;

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
	 * @param scope         the scope this type is defined
	 * @param enclosing     the enclosing element
	 * @param qualifiedName
	 * @param inputStream   the class file input stream  @return the read type element
	 */
	public TypeElem readClass(Scope scope,
	                          Elem enclosing,
	                          EltName qualifiedName,
	                          InputStream inputStream) throws IOException {
		ClassReader reader = new ClassReader(inputStream);
		TypeElemBuilder builder = new TypeElemBuilder(scope, enclosing, qualifiedName);
		reader.accept(builder, ClassReader.SKIP_CODE);
		return builder.getTypeElem();
	}

	class TypeElemBuilder extends ClassVisitor {

		private final Scope scope;
		private final Elem enclosing;
		private final EltName qualifiedName;
		private TypeElem elem;

		public TypeElemBuilder(Scope scope, Elem enclosing, EltName qualifiedName) {
			super(ASM5);
			this.scope = scope;
			this.enclosing = enclosing;
			this.qualifiedName = qualifiedName;
		}

		public TypeElem getTypeElem() {
			return elem;
		}

		@Override
		public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
			elem = new TypeElem(new BinaryOrigin(name), scope, enclosing,
					buildModifiers(access),
					qualifiedName, qualifiedName.simpleName(),
					buildTypeElementKind(access),
					enclosing.getKind() == ElementKind.PACKAGE ? NestingKind.TOP_LEVEL : NestingKind.MEMBER);

		}

		private ElementKind buildTypeElementKind(int access) {
			return hasFlags(access, ACC_INTERFACE, ACC_ANNOTATION) ? ElementKind.ANNOTATION_TYPE :
					hasFlags(access, ACC_INTERFACE) ? ElementKind.INTERFACE :
							ElementKind.CLASS;
		}

		private EnumSet<Modifier> buildModifiers(int access) {
			EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
			if (hasFlag(access, ACC_PUBLIC)) modifiers.add(Modifier.PUBLIC);
			if (hasFlag(access, ACC_PUBLIC)) modifiers.add(Modifier.PROTECTED);
			if (hasFlag(access, ACC_PRIVATE)) modifiers.add(Modifier.PRIVATE);
			if (hasFlag(access, ACC_ABSTRACT)) modifiers.add(Modifier.ABSTRACT);
			if (hasFlag(access, ACC_STATIC)) modifiers.add(Modifier.STATIC);
			if (hasFlag(access, ACC_TRANSIENT)) modifiers.add(Modifier.TRANSIENT);
			if (hasFlag(access, ACC_VOLATILE)) modifiers.add(Modifier.VOLATILE);
			if (hasFlag(access, ACC_SYNCHRONIZED)) modifiers.add(Modifier.SYNCHRONIZED);
			if (hasFlag(access, ACC_NATIVE)) modifiers.add(Modifier.NATIVE);
			if (hasFlag(access, ACC_STRICT)) modifiers.add(Modifier.STRICTFP);
			return modifiers;
		}

		@Override
		public void visitSource(String source, String debug) {
		}

		@Override
		public void visitOuterClass(String owner, String name, String desc) {
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			// TODO Implement
			return null;
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// TODO Implement
			return null;
		}

		@Override
		public void visitInnerClass(String name, String outerName, String innerName, int access) {
		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			return super.visitField(access, name, desc, signature, value);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
			return super.visitMethod(access, name, desc, signature, exceptions);
		}

		@Override
		public void visitAttribute(Attribute attr) {
		}

		@Override
		public void visitEnd() {
		}
	}

	private boolean hasFlag(int flags, int flag) {
		return (flags & flag) == flag;
	}

	private boolean hasFlags(int flags, int... flagsToTest) {
		for (int flag : flagsToTest) {
			if (!hasFlag(flags, flag)) return false;
		}
		return true;
	}
}
