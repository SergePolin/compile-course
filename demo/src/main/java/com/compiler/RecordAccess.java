package com.compiler;

public class RecordAccess extends Expression {
    private String record;
    private String field;

    public RecordAccess(String record, String field) {
        this.record = record;
        this.field = field;
    }

    @Override
    public Object evaluate() {
        // TODO: Implement record field access
        throw new UnsupportedOperationException("Record access not implemented yet");
    }

    @Override
    public String toString() {
        return "RecordAccess\n" +
                "├── " + record + "\n" +
                "└── " + field;
    }
}
