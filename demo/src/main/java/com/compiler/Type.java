package com.compiler;

public abstract class Type {
    protected final String name;

    protected Type(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    // Factory methods for basic types
    public static final SimpleType INTEGER = new SimpleType("integer");
    public static final SimpleType BOOLEAN = new SimpleType("boolean");
    public static final SimpleType STRING = new SimpleType("string");
    public static final SimpleType VOID = new SimpleType("void");

    public static Type fromString(String typeName) {
        switch (typeName) {
            case "integer": return INTEGER;
            case "boolean": return BOOLEAN;
            case "string": return STRING;
            case "void": return VOID;
            default: throw new IllegalArgumentException("Unknown type: " + typeName);
        }
    }
}