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
    public String toString() {
        return "PrintStatement\n" +
                "└── " + expression.toString().replace("\n", "\n    ");
    }

    public void execute() {
        // Assuming Expression has an `evaluate()` method
        System.out.println(expression.evaluate());
    }
}
