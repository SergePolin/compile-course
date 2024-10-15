package com.compiler;

public class LiteralExpression extends Expression {
    private Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return "LiteralExpression(" + value.toString() + ")";
    }

    public Object evaluate() {
        return value;
    }
}
