package com.compiler;

public class FieldAccess extends Expression {
    private final String recordName;
    private final String fieldName;
    private final Type type;

    public FieldAccess(String recordName, String fieldName, Type type) {
        this.recordName = recordName;
        this.fieldName = fieldName;
        this.type = type;
    }

    public String getRecordName() {
        return recordName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Type getType() {
        return type;
    }

    @Override
    public Object evaluate() {
        // Runtime evaluation not supported for record field access
        throw new UnsupportedOperationException("Cannot evaluate record field access at compile time");
    }

    @Override
    public String toString() {
        return recordName + "." + fieldName;
    }
} 