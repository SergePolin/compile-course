package com.compiler;

public class UnaryExpression extends Expression {
    private String operator;
    private Expression expression;

    public UnaryExpression(String operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    @Override
    public Object evaluate() {
        Object value = expression.evaluate();
        if (operator.equals("-")) {
            if (value instanceof Integer) {
                return -(Integer) value;
            } else if (value instanceof Double) {
                return -(Double) value;
            }
        }
        if (operator.equals("not")) {
            if (value instanceof Boolean) {
                return !(Boolean) value;
            }
        }
        throw new RuntimeException("Unsupported unary operation: " + operator);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("UnaryExpression\n");
        sb.append("├── operator: ").append(operator).append("\n");
        sb.append("└── expression: ");
        String[] expressionLines = expression.toString().split("\n");
        if (expressionLines.length > 1) {
            sb.append("\n");
            for (int i = 0; i < expressionLines.length; i++) {
                if (i == expressionLines.length - 1) {
                    sb.append("    └── ");
                } else {
                    sb.append("    ├── ");
                }
                sb.append(expressionLines[i]).append("\n");
            }
        } else {
            sb.append(expression.toString()).append("\n");
        }
        return sb.toString();
    }
}
