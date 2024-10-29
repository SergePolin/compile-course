package com.compiler;

public class RealLiteral extends Expression {
    private double value;

    public RealLiteral(double value) {
        this.value = value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String toString() {
        return "RealLiteral(" + value + ")";
    }
}
