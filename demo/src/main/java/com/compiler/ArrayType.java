package com.compiler;

public class ArrayType extends Type {
    private final Type elementType;
    private final Integer size;  // Can be null for dynamic arrays

    public ArrayType(Type elementType) {
        super("array of " + elementType.toString());
        this.elementType = elementType;
        this.size = null;
    }

    public ArrayType(Type elementType, Integer size) {
        super("array[" + size + "] of " + elementType.toString());
        this.elementType = elementType;
        this.size = size;
    }

    public Type getElementType() {
        return elementType;
    }

    public Integer getSize() {
        return size;
    }
}
