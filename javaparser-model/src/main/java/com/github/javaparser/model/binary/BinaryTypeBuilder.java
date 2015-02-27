package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.element.ElemRef;
import com.github.javaparser.model.element.TypeElem;
import com.github.javaparser.model.element.TypeParameterElem;
import com.github.javaparser.model.scope.EltNames;
import com.github.javaparser.model.type.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
		return classRegistry.typeRef(internalName);
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
	public void feedClassType(String signature, TypeElem elem) {
		SignatureReader reader = new SignatureReader(signature);
		ClassTypeBuilder builder = new ClassTypeBuilder(elem);
		reader.accept(builder);
		builder.visitEnd();
	}

	/**
	 * Builds a generic type mirror for an executable type signature.
	 *
	 * @param signature the generic executable type signature
	 * @return the build mirror
	 */
	public TpeMirror buildExecutableType(String signature) {
		SignatureReader reader = new SignatureReader(signature);
		TypeBuilder builder = new TypeBuilder();
		reader.accept(builder);
		return builder.getType();
	}

	/**
	 * Builds a generic type mirror for a variable type signature.
	 *
	 * @param signature the generic variable type signature
	 * @return the build mirror
	 */
	public TpeMirror buildType(String signature) {
		SignatureReader reader = new SignatureReader(signature);
		TypeBuilder builder = new TypeBuilder();
		reader.acceptType(builder);
		return builder.getType();
	}

	class TypeBuilder extends SignatureVisitor {

		private TpeMirror type;

		public TypeBuilder() {
			super(ASM5);
		}

		public TpeMirror getType() {
			return type;
		}

		@Override
		public void visitBaseType(char descriptor) {
			switch (descriptor) {
				case 'V':
					type = NoTpe.VOID;
					break;
				case 'Z':
					type = PrimitiveTpe.BOOLEAN;
					break;
				case 'C':
					type = PrimitiveTpe.CHAR;
					break;
				case 'B':
					type = PrimitiveTpe.BYTE;
					break;
				case 'S':
					type = PrimitiveTpe.SHORT;
					break;
				case 'I':
					type = PrimitiveTpe.INT;
					break;
				case 'F':
					type = PrimitiveTpe.FLOAT;
					break;
				case 'J':
					type = PrimitiveTpe.LONG;
					break;
				case 'D':
					type = PrimitiveTpe.DOUBLE;
					break;
			}
		}

		@Override
		public void visitTypeVariable(String name) {
			// Use scope
		}

		@Override
		public SignatureVisitor visitArrayType() {
			return new TypeBuilder() {
				@Override
				public void visitEnd() {
					super.visitEnd();
					type = new ArrayTpe(getType());
				}
			};
		}

		private TpeMirror enclosingType = NoTpe.NONE;
		private ElemRef<TypeElem> typeElem;
		private List<TpeMirror> typeArgs = new ArrayList<TpeMirror>();

		@Override
		public void visitClassType(String name) {
			typeElem = resolveType(name);
			typeArgs.clear();
		}

		@Override
		public void visitInnerClassType(String name) {
			enclosingType = closeClassType();
			typeElem = resolveType(name);
			typeArgs.clear();
		}

		@Override
		public void visitTypeArgument() {
			typeArgs.add(new WildcardTpe(null, null));
		}

		@Override
		public SignatureVisitor visitTypeArgument(char wildcard) {
			if (wildcard == '+') {
				return new TypeBuilder() {
					@Override
					public void visitEnd() {
						super.visitEnd();
						typeArgs.add(new WildcardTpe(getType(), null));
					}
				};
			} else {
				return new TypeBuilder() {
					@Override
					public void visitEnd() {
						super.visitEnd();
						typeArgs.add(new WildcardTpe(null, getType()));
					}
				};
			}
		}

		private TpeMirror closeClassType() {
			return new DeclaredTpe(enclosingType, typeElem, typeArgs);
		}

		@Override
		public void visitEnd() {
			type = closeClassType();
		}
	}

	class ClassTypeBuilder extends SignatureVisitor {

		private TypeElem elem;

		public ClassTypeBuilder(TypeElem elem) {
			super(ASM5);
			this.elem = elem;
		}

		private TypeParameterElem typeParam = null;
		private List<TpeMirror> bounds = new ArrayList<TpeMirror>();

		@Override
		public void visitFormalTypeParameter(String name) {
			closeTypeParam();
			typeParam = new TypeParameterElem(new BinaryOrigin(""), elem, EltNames.makeSimple(name));
		}

		private void closeTypeParam() {
			if (typeParam != null) {
				typeParam.setBounds(bounds);
				bounds.clear();
				typeParam = null;
			}
		}


		@Override
		public SignatureVisitor visitClassBound() {
			return new TypeBuilder() {
				@Override
				public void visitEnd() {
					super.visitEnd();
					bounds.add(getType());
				}
			};
		}

		@Override
		public SignatureVisitor visitInterfaceBound() {
			return new TypeBuilder() {
				@Override
				public void visitEnd() {
					super.visitEnd();
					bounds.add(getType());
				}
			};
		}

		@Override
		public SignatureVisitor visitSuperclass() {
			closeTypeParam();

			return new TypeBuilder() {
				@Override
				public void visitEnd() {
					super.visitEnd();
					elem.setSuperClass(getType());
				}
			};
		}

		private List<TpeMirror> interfaces = new ArrayList<TpeMirror>();

		@Override
		public SignatureVisitor visitInterface() {
			return new TypeBuilder() {
				@Override
				public void visitEnd() {
					super.visitEnd();
					interfaces.add(getType());
				}
			};
		}

		@Override
		public void visitEnd() {
			elem.setInterfaces(interfaces);
		}
	}
}
