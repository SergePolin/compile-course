package com.compiler;

public abstract class Expression {
    public abstract Object evaluate();

    @Override
    public abstract String toString();
}
