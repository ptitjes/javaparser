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

package com.github.javaparser.ast.body;

import com.github.javaparser.ast.lexical.Lexeme;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.visitor.GenericVisitor;
import com.github.javaparser.ast.visitor.VoidVisitor;

/**
 * @author Julio Vilmar Gesser
 */
public final class VariableDeclarator extends Node {

    private VariableDeclaratorId id;

    private Expression init;

    public VariableDeclarator() {
    }

    public VariableDeclarator(VariableDeclaratorId id) {
        setId(id);
    }

    public VariableDeclarator(VariableDeclaratorId id, Expression init) {
    	setId(id);
    	setInit(init);
    }

    public VariableDeclarator(Lexeme first, Lexeme last, VariableDeclaratorId id, Expression init) {
        super(first, last);
        setId(id);
        setInit(init);
    }

    @Override
    public <R, A> R accept(GenericVisitor<R, A> v, A arg) {
        return v.visit(this, arg);
    }

    @Override
    public <A> void accept(VoidVisitor<A> v, A arg) {
        v.visit(this, arg);
    }

    public VariableDeclaratorId getId() {
        return id;
    }

    public Expression getInit() {
        return init;
    }

    public void setId(VariableDeclaratorId id) {
        this.id = id;
		setAsParentNodeOf(this.id);
    }

    public void setInit(Expression init) {
        this.init = init;
		setAsParentNodeOf(this.init);
    }
}
