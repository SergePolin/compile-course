package com.compiler;

public class ReturnStatement extends Statement {
    private Expression expression;

    public ReturnStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public boolean hasExpression() {
        return expression != null;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ReturnStatement\n");
        if (expression != null) {
            sb.append("    └── ").append(expression.toString().replace("\n", "\n    "));
        }
        return sb.toString();
    }
}
