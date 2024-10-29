package com.compiler;

public class VariableExpression extends Expression {
    private String varName;

    public VariableExpression(String varName) {
        this.varName = varName;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VariableExpression\n");
        sb.append("└── name: ").append(varName);
        return sb.toString();
    }

    public Object evaluate() {
        return null;
    }
}
