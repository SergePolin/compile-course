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
        return name;
    }
}
