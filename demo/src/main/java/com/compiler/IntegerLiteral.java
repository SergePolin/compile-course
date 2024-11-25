package com.compiler;

public class IntegerLiteral extends Expression {
    private int value;

    public IntegerLiteral(int value) {
        this.value = value;
    }

    public int getValue() {
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
