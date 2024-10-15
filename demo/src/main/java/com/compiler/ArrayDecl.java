package com.compiler;

public class ArrayDecl extends Statement {
    private String identifier;
    private int size;
    private String elementType;

    public ArrayDecl(String identifier, int size, String elementType) {
        this.identifier = identifier;
        this.size = size;
        this.elementType = elementType;
    }

    public String getIdentifier() {
        return identifier;
    }

    public int getSize() {
        return size;
    }

    public String getElementType() {
        return elementType;
    }

    @Override
    public String toString() {
        return "ArrayDecl(\n" +
                "  identifier: " + identifier + "\n" +
                "  size: " + size + "\n" +
                "  elementType: " + elementType + "\n" +
                ")";
    }
}