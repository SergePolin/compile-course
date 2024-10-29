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

    public Expression getLeft() {
        return left;
    }

    @Override
    public Object evaluate() {
        Object leftVal = left.evaluate();
        Object rightVal = right.evaluate();

        switch (operator) {
            case "+":
                return add(leftVal, rightVal);
            case "-":
                return subtract(leftVal, rightVal);
            case "*":
                return multiply(leftVal, rightVal);
            case "/":
                return divide(leftVal, rightVal);
            case "%":
                return mod(leftVal, rightVal);
            case "and":
                return and(leftVal, rightVal);
            case "or":
                return or(leftVal, rightVal);
            case "xor":
                return xor(leftVal, rightVal);
            case "=":
                return equals(leftVal, rightVal);
            case "!=":
                return notEquals(leftVal, rightVal);
            case "<":
                return lessThan(leftVal, rightVal);
            case "<=":
                return lessOrEqual(leftVal, rightVal);
            case ">":
                return greaterThan(leftVal, rightVal);
            case ">=":
                return greaterOrEqual(leftVal, rightVal);
            default:
                throw new RuntimeException("Unknown operator: " + operator);
        }
    }

    // Helper methods for operations
    private Object add(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left + (Integer) right;
        }
        // Add other type combinations as needed
        throw new RuntimeException("Invalid operand types for +");
    }

    // Implement other operation methods similarly
    private Object subtract(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left - (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for -");
    }

    private Object multiply(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left * (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for *");
    }

    private Object divide(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left / (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for /");
    }

    private Object mod(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left % (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for %");
    }

    private Object and(Object left, Object right) {
        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left && (Boolean) right;
        }
        throw new RuntimeException("Invalid operand types for and");
    }

    private Object or(Object left, Object right) {
        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left || (Boolean) right;
        }
        throw new RuntimeException("Invalid operand types for or");
    }

    private Object xor(Object left, Object right) {
        if (left instanceof Boolean && right instanceof Boolean) {
            return (Boolean) left ^ (Boolean) right;
        }
        throw new RuntimeException("Invalid operand types for xor");
    }

    private Object notEquals(Object left, Object right) {
        return !left.equals(right);
    }

    private Object lessThan(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left < (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for <");
    }

    private Object lessOrEqual(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left <= (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for <=");
    }

    private Object greaterThan(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left > (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for >");
    }

    private Object greaterOrEqual(Object left, Object right) {
        if (left instanceof Integer && right instanceof Integer) {
            return (Integer) left >= (Integer) right;
        }
        throw new RuntimeException("Invalid operand types for >=");
    }

    private Object equals(Object left, Object right) {
        return left.equals(right);
    }

    @Override
    public String toString() {
        return String.format("(%s %s %s)", left, operator, right);
    }
}
