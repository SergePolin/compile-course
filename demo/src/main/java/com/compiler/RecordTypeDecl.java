package com.compiler;

import java.util.List;

public class RecordTypeDecl extends Statement {
    private final String name;
    private final List<VarDecl> fields;

    public RecordTypeDecl(String name, List<VarDecl> fields) {
        this.name = name;
        this.fields = fields;
    }

    public String getName() {
        return name;
    }

    public List<VarDecl> getFields() {
        return fields;
    }

    @Override
    public String toString() {
        return "RecordTypeDecl(" + name + ")";
    }
} 