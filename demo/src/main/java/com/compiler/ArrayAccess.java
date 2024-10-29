package com.compiler;

public class ArrayAccess extends Expression {
    private String array;
    private Expression index;

    public ArrayAccess(String array, Expression index) {
        this.array = array;
        this.index = index;
    }

    @Override
    public Object evaluate() {
        // TODO: Implement array access
        throw new UnsupportedOperationException("Array access not implemented yet");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ArrayAccess\n");
        sb.append("├── Array: ").append(array).append("\n");
        sb.append("└── Index: ").append(index);
        return sb.toString();
    }
}
