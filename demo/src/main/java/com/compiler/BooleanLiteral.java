package com.compiler;

public class BooleanLiteral extends Expression {
    private boolean value;

    public BooleanLiteral(boolean value) {
        this.value = value;
    }

    public boolean getValue() {
        return value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String toString() {
        return String.valueOf(value);
    }
}
