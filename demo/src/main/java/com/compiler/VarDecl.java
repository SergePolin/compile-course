package com.compiler;

public class VarDecl extends Statement implements VariableDeclaration {
    private String name;
    private Type type;
    private Expression initializer;

    public VarDecl(String name, Type type, Expression initializer) {
        this.name = name;
        this.type = type;
        this.initializer = initializer;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    public Expression getInitializer() {
        return initializer;
    }

    public void setInitializer(Expression initializer) {
        this.initializer = initializer;
    }

    @Override
    public String toString() {
        return String.format("VarDecl(%s: %s%s)",
                name,
                type,
                initializer != null ? " = " + initializer : "");
    }
}
