package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.element.ElemRef;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.type.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.Collections;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Didier Villevalois
 */
public class BinaryTypeBuilder implements Registry.Participant {

	private ClassRegistry classRegistry;

	@Override
	public void configure(Registry registry) {
		classRegistry = registry.get(ClassRegistry.class);
	}

	private ElemRef<TypeElem> resolveType(String internalName) {
		// Internal names are of the form java/lang/String
		// Delegate to ClassRegistry
		return null;
	}

	/**
	 * Builds a non-generic type mirror.
	 *
	 * @param type the non-generic type
	 * @return the build mirror
	 */
	public TpeMirror buildType(Type type) {
		switch (type.getSort()) {
			case Type.BOOLEAN:
				return PrimitiveTpe.BOOLEAN;
			case Type.BYTE:
				return PrimitiveTpe.BYTE;
			case Type.SHORT:
				return PrimitiveTpe.SHORT;
			case Type.INT:
				return PrimitiveTpe.INT;
			case Type.LONG:
				return PrimitiveTpe.LONG;
			case Type.CHAR:
				return PrimitiveTpe.CHAR;
			case Type.FLOAT:
				return PrimitiveTpe.FLOAT;
			case Type.DOUBLE:
				return PrimitiveTpe.DOUBLE;

			case Type.VOID:
				return NoTpe.VOID;

			case Type.ARRAY:
				int dimensions = type.getDimensions();
				TpeMirror mirror = buildType(type.getElementType());
				while (dimensions > 0) {
					mirror = new ArrayTpe(mirror);
					dimensions--;
				}
				return mirror;

			case Type.OBJECT:
				ElemRef<TypeElem> elemRef = resolveType(type.getInternalName());
				// TODO Check and do something when type is not top-level
				return new DeclaredTpe(NoTpe.NONE, elemRef, Collections.<TpeMirror>emptyList());

			case Type.METHOD:
				// Is this for method types in method calls ?
				return null;

			default:
				// Not reachable
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Builds a generic type mirror for a class type signature.
	 *
	 * @param signature the generic class type signature
	 * @return the build mirror
	 */
	public TpeMirror buildClassType(String signature) {
		SignatureReader reader = new SignatureReader(signature);
		GenericTypeBuilder builder = new GenericTypeBuilder();
		reader.accept(builder);
		return builder.getType();
	}

	/**
	 * Builds a generic type mirror for an executable type signature.
	 *
	 * @param signature the generic executable type signature
	 * @return the build mirror
	 */
	public TpeMirror buildExecutableType(String signature) {
		SignatureReader reader = new SignatureReader(signature);
		GenericTypeBuilder builder = new GenericTypeBuilder();
		reader.accept(builder);
		return builder.getType();
	}

	/**
	 * Builds a generic type mirror for a variable type signature.
	 *
	 * @param signature the generic variable type signature
	 * @return the build mirror
	 */
	public TpeMirror buildVariableType(String signature) {
		SignatureReader reader = new SignatureReader(signature);
		GenericTypeBuilder builder = new GenericTypeBuilder();
		reader.acceptType(builder);
		return builder.getType();
	}

	class GenericTypeBuilder extends SignatureVisitor {

		public GenericTypeBuilder() {
			super(ASM5);
		}

		public TpeMirror getType() {
			return null;
		}

		// ...
	}
}
