package com.compiler;

/**
 * Represents a type cast expression in the AST.
 * This node handles the conversion of expressions between different types (real, integer, boolean).
 */
public class TypeCastExpression extends Expression {
    private Expression expression;
    private String targetType;

    /**
     * Creates a new type cast expression.
     *
     * @param expression The expression to be cast
     * @param targetType The type to cast the expression to ("real", "integer", or "boolean")
     */
    public TypeCastExpression(Expression expression, String targetType) {
        this.expression = expression;
        this.targetType = targetType;
    }

    /**
     * Gets the expression being cast.
     *
     * @return The expression being cast
     */
    public Expression getExpression() {
        return expression;
    }

    /**
     * Gets the target type for the cast.
     *
     * @return The target type as a string
     */
    public String getTargetType() {
        return targetType;
    }

    @Override
    public String toString() {
        return "TypeCast[" + targetType + "](\n" +
                "  " + expression.toString().replace("\n", "\n  ") + "\n)";
    }

    /**
     * Evaluates the type cast expression by converting the value of the inner expression
     * to the target type.
     *
     * @return The converted value
     * @throws RuntimeException if the cast operation is invalid
     */
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
