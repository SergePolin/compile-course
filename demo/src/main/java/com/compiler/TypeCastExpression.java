package com.compiler;

public class TypeCastExpression extends Expression {
    private Expression expression;
    private String targetType;

    public TypeCastExpression(Expression expression, String targetType) {
        this.expression = expression;
        this.targetType = targetType;
    }

    public Expression getExpression() {
        return expression;
    }

    public String getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return "(" + targetType + ")\n" +
                "  " + expression.toString().replace("\n", "\n  ");
    }

    // Example evaluate method (you may adjust based on your needs)
    public Object evaluate() {
        Object value = expression.evaluate();
        switch (targetType) {
            case "real":
                if (value instanceof Number) {
                    return ((Number) value).doubleValue();
                }
                break;
            case "integer":
                if (value instanceof Number) {
                    return ((Number) value).intValue();
                } else if (value instanceof Boolean) {
                    return ((Boolean) value) ? 1 : 0;
                }
                break;
            case "boolean":
                if (value instanceof Number) {
                    return ((Number) value).doubleValue() != 0.0;
                }
                break;
        }
        throw new RuntimeException(
                "Invalid cast from " + (value != null ? value.getClass().getName() : "null") + " to " + targetType);
    }
}
