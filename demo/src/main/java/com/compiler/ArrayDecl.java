package com.compiler;

public class ArrayDecl extends Statement implements VariableDeclaration {
    private String name;
    private ArrayType type;

    public ArrayDecl(String name, ArrayType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return "ArrayDecl\n"
                + "├── name: " + name + "\n"
                + "└── type: " + type;
    }
}