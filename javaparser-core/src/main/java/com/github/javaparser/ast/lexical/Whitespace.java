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

package com.github.javaparser.ast.lexical;

import java.util.EnumSet;

/**
 * @author Didier Villevalois
 */
public class Whitespace extends Lexeme {

    private final WhitespaceKind kind;
    private final String image;

    public Whitespace(WhitespaceKind kind, String image) {
        this.kind = kind;
        this.image = image;
    }

    @Override
    public LexemeKind getLexemeKind() {
        return LexemeKind.WHITESPACE;
    }

    public WhitespaceKind getWhitespaceKind() {
        return kind;
    }

    @Override
    public boolean is(WhitespaceKind first, WhitespaceKind... rest) {
        return EnumSet.of(first, rest).contains(getWhitespaceKind());
    }

    @Override
    public String image() {
        return image;
    }
}
