package com.compiler;

public class TypeCast extends Expression {
    private Expression expr;
    private Type targetType;

    public TypeCast(Expression expr, Type targetType) {
        this.expr = expr;
        this.targetType = targetType;
    }

    @Override
    public Object evaluate() {
        Object value = expr.evaluate();
        // TODO: Implement type conversion
        throw new UnsupportedOperationException("Type casting not implemented yet");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("TypeCast\n");
        sb.append("├── expr: ").append(expr).append("\n");
        sb.append("└── targetType: ").append(targetType).append("\n");
        return sb.toString();
    }
}
