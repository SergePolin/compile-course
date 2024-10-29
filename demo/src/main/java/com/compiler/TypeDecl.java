package com.compiler;

public class TypeDecl extends Statement {
    private String name;
    private Type type;

    public TypeDecl(String name, Type type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TypeDecl\n"
                + "├── name: " + name + "\n"
                + "└── type: " + type;
    }
}
