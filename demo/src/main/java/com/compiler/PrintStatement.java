package com.compiler;

public class PrintStatement extends Statement {
    private Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public void execute() {
        Object value = expression.evaluate();
        System.out.println(value);
    }

    @Override
    public String toString() {
        return "PrintStatement\n└── " + expression.toString();
    }
}
