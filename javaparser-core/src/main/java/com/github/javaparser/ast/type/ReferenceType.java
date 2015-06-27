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

package com.github.javaparser.ast.type;

import com.github.javaparser.ast.expr.AnnotationExpr;

import java.util.List;

/**
 * @author Julio Vilmar Gesser
 */
public abstract class ReferenceType extends Type {

    public ReferenceType() {
        super();
    }

    public ReferenceType(List<AnnotationExpr> annotations) {
        super(annotations);
    }

    public ReferenceType(final int beginLine, final int beginColumn, final int endLine, final int endColumn) {
        super(beginLine, beginColumn, endLine, endColumn);
    }

    public ReferenceType(final int beginLine, final int beginColumn, final int endLine, final int endColumn,
                         List<AnnotationExpr> annotations) {
        super(beginLine, beginColumn, endLine, endColumn, annotations);
    }
}
