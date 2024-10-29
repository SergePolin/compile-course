package com.compiler;

import java.util.List;

public class RecordType extends Type {
    private List<VariableDeclaration> fields;

    public RecordType(List<VariableDeclaration> fields) {
        this.fields = fields;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("record {\n");
        for (VariableDeclaration field : fields) {
            sb.append("  ").append(field).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
