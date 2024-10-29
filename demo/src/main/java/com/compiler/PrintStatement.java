package com.compiler;

public class PrintStatement extends Statement {
    private Expression expression;

    public PrintStatement(Expression expression) {
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    public void execute() {
        Object value = expression.evaluate();
        System.out.println(value);
    }

    @Override
    public String toString() {
        return "Print\n" +
                "└── " + expression.toString().replace("\n", "\n    ");
    }
}
