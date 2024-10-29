package com.compiler;

public class UnaryExpression extends Expression {
    private String operator;
    private Expression expression;

    public UnaryExpression(String operator, Expression expression) {
        this.operator = operator;
        this.expression = expression;
    }

    public Expression getExpression() {
        return expression;
    }

    @Override
    public Object evaluate() {
        Object value = expression.evaluate();
        switch (operator) {
            case "-":
                return negate(value);
            case "not":
                return not(value);
            default:
                throw new RuntimeException("Unknown unary operator: " + operator);
        }
    }

    private Object negate(Object value) {
        if (value instanceof Integer) {
            return -(Integer) value;
        }
        throw new RuntimeException("Invalid operand type for unary -");
    }

    private Object not(Object value) {
        if (value instanceof Boolean) {
            return !(Boolean) value;
        }
        throw new RuntimeException("Invalid operand type for not");
    }

    @Override
    public String toString() {
        return String.format("(%s %s)", operator, expression);
    }
}
