package com.compiler;

public class VariableReference extends Expression {
    private String name;

    public VariableReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public Object evaluate() {
        // TODO: Implement symbol table lookup
        throw new UnsupportedOperationException("Variable lookup not implemented yet");
    }

    @Override
    public String toString() {
        return "VariableReference(" + name + ")";
    }
}