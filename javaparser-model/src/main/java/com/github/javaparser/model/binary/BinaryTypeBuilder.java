package com.github.javaparser.model.binary;

import com.github.javaparser.model.Registry;
import com.github.javaparser.model.element.*;
import com.github.javaparser.model.scope.EltNames;
import com.github.javaparser.model.scope.Scope;
import com.github.javaparser.model.type.*;
import org.objectweb.asm.Type;
import org.objectweb.asm.signature.SignatureReader;
import org.objectweb.asm.signature.SignatureVisitor;

import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.Parameterizable;
import javax.lang.model.element.VariableElement;
import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumSet;
import java.util.List;

import static org.objectweb.asm.Opcodes.ASM5;

/**
 * @author Didier Villevalois
 */
public class BinaryTypeBuilder implements Registry.Participant {

	private ClassRegistry classRegistry;
	private TypeUtils typeUtils;

	@Override
	public void configure(Registry registry) {
		classRegistry = registry.get(ClassRegistry.class);
		typeUtils = registry.get(TypeUtils.class);
	}

	private ElemRef<TypeElem> resolveType(String internalName) {
		// Internal names are of the form java/lang/String
		return classRegistry.typeRef(internalName);
	}

	/**
	 * Builds a non-generic result mirror.
	 *
	 * @param type the non-generic result
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
				// TODO Check and do something when result is not top-level
				return new DeclaredTpe(NoTpe.NONE, elemRef, Collections.<TpeMirror>emptyList());

			case Type.METHOD:
				return null;

			default:
				// Not reachable
				throw new IllegalArgumentException();
		}
	}

	/**
	 * Builds a generic result mirror for a class result signature.
	 *
	 * @param signature the generic class result signature
	 * @return the build mirror
	 */
	public void feedClassType(String signature, String superName, String[] interfaces, TypeElem elem) {
		if (signature != null) {
			SignatureReader reader = new SignatureReader(signature);

			feedTypeParameters(elem, reader);

			ClassTypeBuilder builder = new ClassTypeBuilder(elem);
			reader.accept(builder);

			builder.feedTypes();
		} else {
			if (superName != null) {
				elem.setSuperClass(new DeclaredTpe(NoTpe.NONE, resolveType(superName), Collections.<TpeMirror>emptyList()));
			} else {
				elem.setSuperClass(NoTpe.NONE);
			}
			if (interfaces != null) {
				List<TpeMirror> interfaceMirrors = new ArrayList<TpeMirror>();
				for (String anInterface : interfaces) {
					interfaceMirrors.add(new DeclaredTpe(NoTpe.NONE, resolveType(anInterface), Collections.<TpeMirror>emptyList()));
				}
				elem.setInterfaces(interfaceMirrors);
			} else {
				elem.setInterfaces(Collections.<TpeMirror>emptyList());
			}
		}
	}

	/**
	 * Builds a generic result mirror for an executable result signature.
	 *
	 * @param signature the generic executable result signature
	 * @return the build mirror
	 */
	public void feedExecutableType(String desc, String signature, String[] exceptions, ExecutableElem elem) {
		if (signature != null) {
			SignatureReader reader = new SignatureReader(signature);

			feedTypeParameters(elem, reader);

			ExecutableTypeBuilder builder = new ExecutableTypeBuilder(elem);
			reader.accept(builder);

			builder.feedTypes();
		} else {
			// TODO Read desc and feed parameters and return result
			Type methodType = Type.getType(desc);

			elem.setReturnType(buildType(methodType.getReturnType()));

			int parameterIndex = 0;
			for (Type argumentType : methodType.getArgumentTypes()) {
				VariableElem parameter = new VariableElem(new BinaryOrigin(""), elem,
						EnumSet.noneOf(Modifier.class),
						EltNames.makeSimple("arg" + (parameterIndex++)),
						ElementKind.PARAMETER);
				parameter.setType(buildType(argumentType));
			}

			if (exceptions != null) {
				List<TpeMirror> exceptionMirrors = new ArrayList<TpeMirror>();
				for (String exception : exceptions) {
					exceptionMirrors.add(new DeclaredTpe(NoTpe.NONE, resolveType(exception), Collections.<TpeMirror>emptyList()));
				}
				elem.setThrownTypes(exceptionMirrors);
			} else {
				elem.setThrownTypes(Collections.<TpeMirror>emptyList());
			}
		}
	}

	private <E extends Elem & Parameterizable> void feedTypeParameters(E elem, SignatureReader reader) {
		TypeParametersBuilder typeParametersBuilder = new TypeParametersBuilder<E>(elem);
		reader.accept(typeParametersBuilder);

		TypeParameterBoundsFeeder typeParameterBoundsFeeder = new TypeParameterBoundsFeeder<E>(elem);
		reader.accept(typeParameterBoundsFeeder);
		typeParameterBoundsFeeder.finish();
	}

	/**
	 * Builds a generic result mirror for a variable result signature.
	 *
	 * @param signature the generic variable result signature
	 * @return the build mirror
	 */
	public void feedVariableType(String desc, String signature, VariableElem elem) {
		if (signature != null) {
			elem.setType(buildType(signature, elem.scope()));
		} else {
			elem.setType(buildType(Type.getType(desc)));
		}
	}

	/**
	 * Builds a generic result mirror for a variable result signature.
	 *
	 * @param signature the generic variable result signature
	 * @return the build mirror
	 */
	public TpeMirror buildType(String signature, Scope scope) {
		SignatureReader reader = new SignatureReader(signature);
		TypeBuilder builder = new TypeBuilder(scope, new TypeCallback() {
			@Override
			public void typeBuilt(TpeMirror type) {
			}
		});
		reader.acceptType(builder);
		return builder.getType();
	}

	interface TypeCallback {
		void typeBuilt(TpeMirror type);
	}

	class TypeBuilder extends SignatureVisitor {

		private final Scope scope;
		private final TypeCallback callback;
		private TpeMirror result;

		public TypeBuilder(Scope scope, TypeCallback callback) {
			super(ASM5);
			this.scope = scope;
			this.callback = callback;
		}

		public TpeMirror getType() {
			return result;
		}

		@Override
		public void visitBaseType(char descriptor) {
			switch (descriptor) {
				case 'V':
					result = NoTpe.VOID;
					break;
				case 'Z':
					result = PrimitiveTpe.BOOLEAN;
					break;
				case 'C':
					result = PrimitiveTpe.CHAR;
					break;
				case 'B':
					result = PrimitiveTpe.BYTE;
					break;
				case 'S':
					result = PrimitiveTpe.SHORT;
					break;
				case 'I':
					result = PrimitiveTpe.INT;
					break;
				case 'F':
					result = PrimitiveTpe.FLOAT;
					break;
				case 'J':
					result = PrimitiveTpe.LONG;
					break;
				case 'D':
					result = PrimitiveTpe.DOUBLE;
					break;
			}
			callback.typeBuilt(result);
		}

		@Override
		public void visitTypeVariable(String name) {
			TypeParameterElem typeParameterElem = scope.resolveTypeParameter(EltNames.makeSimple(name));
			callback.typeBuilt(new TpeVariable(typeParameterElem, typeUtils.objectType()));
		}

		@Override
		public SignatureVisitor visitArrayType() {
			return new TypeBuilder(scope, new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					result = new ArrayTpe(type);
					callback.typeBuilt(result);
				}
			});
		}

		private TpeMirror enclosingType = NoTpe.NONE;
		private ElemRef<TypeElem> typeElem;
		private List<TpeMirror> typeArgs = new ArrayList<TpeMirror>();

		@Override
		public void visitClassType(String name) {
			typeElem = resolveType(name);
			typeArgs = new ArrayList<TpeMirror>();
		}

		@Override
		public void visitInnerClassType(String name) {
			enclosingType = closeClassType();
			typeElem = resolveType(name);
			typeArgs = new ArrayList<TpeMirror>();
		}

		@Override
		public void visitTypeArgument() {
			typeArgs.add(new WildcardTpe(null, null));
		}

		@Override
		public SignatureVisitor visitTypeArgument(char wildcard) {
			if (wildcard == '+') {
				return new TypeBuilder(scope, new TypeCallback() {
					@Override
					public void typeBuilt(TpeMirror type) {
						typeArgs.add(type);
					}
				});
			} else {
				return new TypeBuilder(scope, new TypeCallback() {
					@Override
					public void typeBuilt(TpeMirror type) {
						typeArgs.add(type);
					}
				});
			}
		}

		private TpeMirror closeClassType() {
			return new DeclaredTpe(enclosingType, typeElem, typeArgs);
		}

		@Override
		public void visitEnd() {
			result = closeClassType();
			callback.typeBuilt(result);
		}
	}

	class TypeParametersBuilder<E extends Elem & Parameterizable> extends SignatureVisitor {

		private E elem;

		public TypeParametersBuilder(E elem) {
			super(ASM5);
			this.elem = elem;
		}

		@Override
		public void visitFormalTypeParameter(String name) {
			TypeParameterElem typeParameterElem =
					new TypeParameterElem(new BinaryOrigin(""), elem, EltNames.makeSimple(name));
			typeParameterElem.setType(new TpeVariable(typeParameterElem, typeUtils.objectType()));
		}
	}

	class TypeParameterBoundsFeeder<E extends Elem & Parameterizable> extends SignatureVisitor {

		private E elem;

		public TypeParameterBoundsFeeder(E elem) {
			super(ASM5);
			this.elem = elem;
		}

		private TypeParameterElem typeParam = null;
		private List<TpeMirror> bounds = new ArrayList<TpeMirror>();

		@Override
		public void visitFormalTypeParameter(String name) {
			feedBounds();
			typeParam = elem.scope().resolveTypeParameter(EltNames.makeSimple(name));
		}

		@Override
		public SignatureVisitor visitClassBound() {
			return new TypeBuilder(elem.scope(), new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					bounds.add(type);
				}
			});
		}

		@Override
		public SignatureVisitor visitInterfaceBound() {
			return new TypeBuilder(elem.scope(), new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					bounds.add(type);
				}
			});
		}

		private void feedBounds() {
			if (typeParam != null) {
				typeParam.setBounds(bounds);
				bounds = new ArrayList<TpeMirror>();
				typeParam = null;
			}
		}

		public void finish() {
			feedBounds();
		}
	}

	class ClassTypeBuilder extends SignatureVisitor {

		private TypeElem elem;

		public ClassTypeBuilder(TypeElem elem) {
			super(ASM5);
			this.elem = elem;
		}

		private TpeMirror superClass = NoTpe.NONE;

		@Override
		public SignatureVisitor visitSuperclass() {
			return new TypeBuilder(elem.scope(), new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					superClass = type;
				}
			});
		}

		private List<TpeMirror> interfaces = new ArrayList<TpeMirror>();

		@Override
		public SignatureVisitor visitInterface() {
			return new TypeBuilder(elem.scope(), new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					interfaces.add(type);
				}
			});
		}

		public void feedTypes() {
			elem.setSuperClass(superClass);
			elem.setInterfaces(interfaces);
		}
	}

	class ExecutableTypeBuilder extends SignatureVisitor {

		private ExecutableElem elem;

		public ExecutableTypeBuilder(ExecutableElem elem) {
			super(ASM5);
			this.elem = elem;
		}

		private int parameterIndex = 0;
		private TpeMirror returnType = NoTpe.NONE;
		private List<TpeMirror> exceptions = new ArrayList<TpeMirror>();

		@Override
		public SignatureVisitor visitParameterType() {
			List<? extends VariableElement> parameters = elem.getParameters();
			final VariableElem parameter = parameters.size() <= parameterIndex ?
					new VariableElem(new BinaryOrigin(""), elem,
							EnumSet.noneOf(Modifier.class),
							EltNames.makeSimple("arg" + (parameterIndex++)),
							ElementKind.PARAMETER) :
					(VariableElem) parameters.get(parameterIndex++);

			return new TypeBuilder(elem.scope(), new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					parameter.setType(type);
				}
			});
		}

		@Override
		public SignatureVisitor visitReturnType() {
			return new TypeBuilder(elem.scope(), new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					returnType = type;
				}
			});
		}

		@Override
		public SignatureVisitor visitExceptionType() {
			return new TypeBuilder(elem.scope(), new TypeCallback() {
				@Override
				public void typeBuilt(TpeMirror type) {
					exceptions.add(type);
				}
			});
		}

		public void feedTypes() {
			elem.setReturnType(returnType);
			elem.setThrownTypes(exceptions);
		}
	}
}
