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
        return "ReturnStatement(" + expression + ")";
    }
}
