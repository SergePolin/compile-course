package com.compiler;

public class TypeDecl extends Statement {
    private String id;
    private String type;

    public TypeDecl(String id, String type) {
        this.id = id;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return "TypeDecl\n"
                + "├── id: " + id + "\n"
                + "└── type: " + type;
    }
}
