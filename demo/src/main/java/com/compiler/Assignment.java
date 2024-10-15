package com.compiler;

public class Assignment extends Statement {
    private String variable;
    private Expression expression;

    public Assignment(String variable, Expression expression) {
        this.variable = variable;
        this.expression = expression;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Assignment(\n");
        sb.append("  variable: ").append(variable).append(",\n");
        sb.append("  expression: ").append(expression.toString().replace("\n", "\n  ")).append("\n");
        sb.append(")");
        return sb.toString();
    }
}
