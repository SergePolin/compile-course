package com.compiler;

public class StringLiteral extends Expression {
    private String value;

    public StringLiteral(String value) {
        this.value = value;
    }

    @Override
    public Object evaluate() {
        return value;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("StringLiteral\n");
        sb.append("└── value: \"").append(value).append("\"\n");
        return sb.toString();
    }
}
