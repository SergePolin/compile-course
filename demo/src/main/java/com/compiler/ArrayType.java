package com.compiler;

public class ArrayType extends Type {
    private Type elementType;
    private int size;

    public ArrayType(Type elementType, int size) {
        this.elementType = elementType;
        this.size = size;
    }

    @Override
    public String toString() {
        return "array[" + size + "] of " + elementType;
    }
}
