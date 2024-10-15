package com.compiler;

public class BinaryExpression extends Expression {
    private Expression left;
    private String operator;
    private Expression right;

    public BinaryExpression(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BinaryExpression(\n");
        sb.append("  left: ").append(left.toString().replace("\n", "\n  ")).append(",\n");
        sb.append("  operator: \"").append(operator).append("\",\n");
        sb.append("  right: ").append(right.toString().replace("\n", "\n  ")).append("\n");
        sb.append(")");
        return sb.toString();
    }

    @Override
    public Object evaluate() {
        Object leftValue = left.evaluate();
        Object rightValue = right.evaluate();

        switch (operator) {
            case "+":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return (Integer) leftValue + (Integer) rightValue;
                } else if (leftValue instanceof Double && rightValue instanceof Double) {
                    return (Double) leftValue + (Double) rightValue;
                }
                break;
            case "-":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return (Integer) leftValue - (Integer) rightValue;
                } else if (leftValue instanceof Double && rightValue instanceof Double) {
                    return (Double) leftValue - (Double) rightValue;
                }
                break;
            case "*":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return (Integer) leftValue * (Integer) rightValue;
                } else if (leftValue instanceof Double && rightValue instanceof Double) {
                    return (Double) leftValue * (Double) rightValue;
                }
                break;
            case "/":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return (Integer) leftValue / (Integer) rightValue;
                } else if (leftValue instanceof Double && rightValue instanceof Double) {
                    return (Double) leftValue / (Double) rightValue;
                }
                break;
            case ">":
                return (Integer) leftValue > (Integer) rightValue;
            case "<":
                return (Integer) leftValue < (Integer) rightValue;
        }

        throw new RuntimeException("Unsupported binary operation: " + operator);
    }
}
