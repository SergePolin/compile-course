package com.compiler;

public class ArrayType extends Type {
    private Type elementType;
    private int size;

    public ArrayType(Type elementType, int size) {
        this.elementType = elementType;
        this.size = size;
    }

    public Type getElementType() {
        return elementType;
    }

    public int getSize() {
        return size;
    }

    @Override
    public String toString() {
        return "ArrayType\n"
                + "├── size: " + size + "\n"
                + "└── elementType: " + elementType;
    }
}
