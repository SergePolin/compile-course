package com.compiler;

public class RecordAccess extends Expression {
    private String record;
    private String field;

    public RecordAccess(String record, String field) {
        this.record = record;
        this.field = field;
    }

    public String getRecord() {
        return record;
    }

    public String getField() {
        return field;
    }

    @Override
    public Object evaluate() {
        return null;
    }

    @Override
    public String toString() {
        return record + "." + field;
    }
}
