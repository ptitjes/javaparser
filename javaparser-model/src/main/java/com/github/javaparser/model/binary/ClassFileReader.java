package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.element.*;
import com.github.javaparser.model.scope.EltName;
import com.github.javaparser.model.scope.EltNames;
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
			ElementKind kind = buildTypeElementKind(access);
			elem = new TypeElem(new BinaryOrigin(name), scope, enclosing,
					buildModifiers(access),
					qualifiedName, qualifiedName.simpleName(),
					kind,
					enclosing.getKind() == ElementKind.PACKAGE ? NestingKind.TOP_LEVEL : NestingKind.MEMBER);

			typeBuilder.feedClassType(signature, superName, interfaces, elem);
		}

		private ElementKind buildTypeElementKind(int access) {
			return hasFlags(access, ACC_INTERFACE, ACC_ANNOTATION) ? ElementKind.ANNOTATION_TYPE :
					hasFlags(access, ACC_INTERFACE) ? ElementKind.INTERFACE :
							ElementKind.CLASS;
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
		public void visitAttribute(Attribute attr) {
		}

		@Override
		public void visitInnerClass(String name, String outerName, String innerName, int access) {
		}

		@Override
		public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
			VariableElem variable = new VariableElem(new BinaryOrigin(""),
					elem,
					buildModifiers(access),
					EltNames.makeSimple(name),
					elem.getKind() == ElementKind.ENUM ? ElementKind.ENUM_CONSTANT : ElementKind.FIELD);

			typeBuilder.feedVariableType(desc, signature, variable);

			// TODO Process constant value (value)

			return new FieldElemBuilder(variable);
		}

		@Override
		public MethodVisitor visitMethod(int access, String name,
		                                 final String desc, final String signature, final String[] exceptions) {
			final ExecutableElem executable = new ExecutableElem(new BinaryOrigin(""),
					elem,
					buildModifiers(access),
					EltNames.makeSimple(name),
					name.equals("<init>") ? ElementKind.CONSTRUCTOR :
							name.equals("<clinit>") ? ElementKind.STATIC_INIT :
									ElementKind.METHOD);
			// TODO How to recognize an instance initializer ?

			return new MethodElemBuilder(executable) {
				@Override
				public void visitEnd() {
					typeBuilder.feedExecutableType(desc, signature, exceptions, executable);
				}
			};
		}

		@Override
		public void visitEnd() {
		}
	}

	class FieldElemBuilder extends FieldVisitor {
		public FieldElemBuilder(VariableElem elem) {
			super(ASM5);
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			// TODO Implement
			return null;
		}

		@Override
		public void visitAttribute(Attribute attr) {
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// TODO Implement
			return null;
		}

		@Override
		public void visitEnd() {
		}
	}

	class MethodElemBuilder extends MethodVisitor {

		private final ExecutableElem elem;

		public MethodElemBuilder(ExecutableElem elem) {
			super(ASM5);
			this.elem = elem;
		}

		@Override
		public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
			// TODO Implement
			return null;
		}

		@Override
		public void visitAttribute(Attribute attr) {
		}

		@Override
		public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
			// TODO Implement
			return null;
		}

		@Override
		public void visitParameter(String name, int access) {
			new VariableElem(new BinaryOrigin(""), elem,
					buildModifiers(access),
					EltNames.makeSimple(name),
					ElementKind.PARAMETER);
		}

		@Override
		public AnnotationVisitor visitParameterAnnotation(int parameter, String desc, boolean visible) {
			// TODO Implement
			return null;
		}

		@Override
		public AnnotationVisitor visitAnnotationDefault() {
			// TODO Implement
			return null;
		}

		@Override
		public void visitEnd() {
		}
	}

	private EnumSet<Modifier> buildModifiers(int access) {
		EnumSet<Modifier> modifiers = EnumSet.noneOf(Modifier.class);
		if (hasFlag(access, ACC_PUBLIC)) modifiers.add(Modifier.PUBLIC);
		if (hasFlag(access, ACC_PROTECTED)) modifiers.add(Modifier.PROTECTED);
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
