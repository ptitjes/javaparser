/*
 * Copyright (C) 2007-2010 Júlio Vilmar Gesser.
 * Copyright (C) 2011, 2013-2015 The JavaParser Team.
 *
 * This file is part of JavaParser.
 *
 * JavaParser is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * JavaParser is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with JavaParser.  If not, see <http://www.gnu.org/licenses/>.
 */

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
