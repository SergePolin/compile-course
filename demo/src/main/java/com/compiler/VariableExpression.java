package com.compiler;

public class VariableExpression extends Expression {
    private String varName;

    public VariableExpression(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        return "VariableExpression(" + varName + ")";
    }

    public Object evaluate() {
        return null;
    }
}
