package com.github.javaparser.model.source.utils;

import com.github.javaparser.ast.body.ModifierSet;

import javax.lang.model.element.Modifier;
import java.util.EnumSet;
import java.util.Set;

/**
 * @author Didier Villevalois
 */
public abstract class ModifiersUtils {

	public static Set<Modifier> convert(int modifiers) {
		EnumSet<Modifier> converted = EnumSet.noneOf(Modifier.class);
		if (ModifierSet.isAbstract(modifiers)) converted.add(Modifier.ABSTRACT);
		if (ModifierSet.isFinal(modifiers)) converted.add(Modifier.FINAL);
		if (ModifierSet.isNative(modifiers)) converted.add(Modifier.NATIVE);
		if (ModifierSet.isPublic(modifiers)) converted.add(Modifier.PUBLIC);
		else if (ModifierSet.isPrivate(modifiers)) converted.add(Modifier.PRIVATE);
		else if (ModifierSet.isProtected(modifiers)) converted.add(Modifier.PROTECTED);
		if (ModifierSet.isStatic(modifiers)) converted.add(Modifier.STATIC);
		if (ModifierSet.isStrictfp(modifiers)) converted.add(Modifier.STRICTFP);
		if (ModifierSet.isSynchronized(modifiers)) converted.add(Modifier.SYNCHRONIZED);
		if (ModifierSet.isTransient(modifiers)) converted.add(Modifier.TRANSIENT);
		if (ModifierSet.isVolatile(modifiers)) converted.add(Modifier.VOLATILE);
		return converted;
	}
}
