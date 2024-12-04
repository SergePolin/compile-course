package com.compiler;

/**
 * Represents a binary operation in the AST (e.g., addition, subtraction, etc.)
 */
public class BinaryOperation extends Expression {
    private final Expression left;
    private final String operator;
    private final Expression right;

    public BinaryOperation(Expression left, String operator, Expression right) {
        this.left = left;
        this.operator = operator;
        this.right = right;
    }

    public Expression getLeft() {
        return left;
    }

    public String getOperator() {
        return operator;
    }

    public Expression getRight() {
        return right;
    }

    @Override
    public String toString() {
        return "(" + left + " " + operator + " " + right + ")";
    }

    @Override
    public Object evaluate() {
        Object leftValue = left.evaluate();
        Object rightValue = right.evaluate();
        
        if (leftValue == null || rightValue == null) {
            return null;
        }

        switch (operator) {
            case "+":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return (Integer) leftValue + (Integer) rightValue;
                } else if (leftValue instanceof String || rightValue instanceof String) {
                    return String.valueOf(leftValue) + String.valueOf(rightValue);
                }
                break;
            case "-":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return (Integer) leftValue - (Integer) rightValue;
                }
                break;
            case "*":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    return (Integer) leftValue * (Integer) rightValue;
                }
                break;
            case "/":
                if (leftValue instanceof Integer && rightValue instanceof Integer) {
                    if ((Integer) rightValue == 0) {
                        throw new ArithmeticException("Division by zero");
                    }
                    return (Integer) leftValue / (Integer) rightValue;
                }
                break;
            case "and":
                if (leftValue instanceof Boolean && rightValue instanceof Boolean) {
                    return (Boolean) leftValue && (Boolean) rightValue;
                }
                break;
            case "or":
                if (leftValue instanceof Boolean && rightValue instanceof Boolean) {
                    return (Boolean) leftValue || (Boolean) rightValue;
                }
                break;
        }
        
        throw new RuntimeException("Invalid operation: " + operator + 
                                 " between " + leftValue + " and " + rightValue);
    }
} 