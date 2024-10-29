package com.compiler;

public class SimpleType extends Type {
    private String name;

    public SimpleType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("SimpleType\n");
        sb.append("└── name: ").append(name).append("\n");
        return sb.toString();
    }
}
