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
        StringBuilder sb = new StringBuilder();
        sb.append("type ").append(name).append(" is record\n");
        for (VarDecl field : fields) {
            sb.append("    ").append(field).append("\n");
        }
        sb.append("end");
        return sb.toString();
    }
} 