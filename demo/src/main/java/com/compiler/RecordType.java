package com.compiler;

import java.util.*;

public class RecordType extends Type {
    private Map<String, Type> fields;
    private List<VariableDeclaration> fieldDeclarations;
    
    public RecordType(List<VariableDeclaration> fieldDecls) {
        this.fieldDeclarations = fieldDecls;
        this.fields = new HashMap<>();
        for (VariableDeclaration decl : fieldDecls) {
            fields.put(decl.getName(), decl.getType());
        }
    }
    
    public boolean hasField(String fieldName) {
        return fields.containsKey(fieldName);
    }
    
    public Type getFieldType(String fieldName) {
        return fields.get(fieldName);
    }
    
    public Set<String> getFieldNames() {
        return fields.keySet();
    }
    
    public List<VariableDeclaration> getFields() {
        return fieldDeclarations;
    }
    
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("record {\n");
        for (VariableDeclaration field : fieldDeclarations) {
            sb.append("  ").append(field.toString()).append("\n");
        }
        sb.append("}");
        return sb.toString();
    }
}
