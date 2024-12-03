package com.compiler;

public class FieldAssignment extends Statement {
    private final String recordName;
    private final String fieldName;
    private final Expression value;
    private final Type type;

    public FieldAssignment(String recordName, String fieldName, Expression value, Type type) {
        this.recordName = recordName;
        this.fieldName = fieldName;
        this.value = value;
        this.type = type;
    }

    public String getRecordName() {
        return recordName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public Expression getValue() {
        return value;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return recordName + "." + fieldName + " := " + value;
    }
} 