package com.compiler;

/**
 * Represents a field access expression in the source code.
 * A field access consists of a record name and a field name, used to access a field within a record type.
 */
public class FieldAccess extends Expression {
    /** The name of the record being accessed */
    private final String recordName;
    /** The name of the field being accessed */
    private final String fieldName;
    /** The type of the field */
    private final Type type;

    /**
     * Creates a new field access expression.
     * @param recordName The name of the record containing the field
     * @param fieldName The name of the field to access
     * @param type The type of the field being accessed
     */
    public FieldAccess(String recordName, String fieldName, Type type) {
        this.recordName = recordName;
        this.fieldName = fieldName;
        this.type = type;
    }

    /**
     * Gets the name of the record being accessed.
     * @return The record name
     */
    public String getRecordName() {
        return recordName;
    }

    /**
     * Gets the name of the field being accessed.
     * @return The field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the type of the field.
     * @return The field's type
     */
    public Type getType() {
        return type;
    }

    /**
     * Evaluates this field access expression.
     * @return The value of the field
     * @throws UnsupportedOperationException as record field access cannot be evaluated at compile time
     */
    @Override
    public Object evaluate() {
        // Runtime evaluation not supported for record field access
        throw new UnsupportedOperationException("Cannot evaluate record field access at compile time");
    }

    /**
     * Returns a string representation of this field access in tree format.
     * @return A hierarchical string representation showing the record and field names
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("FieldAccess\n");
        sb.append("├── Record: ").append(recordName).append("\n");
        sb.append("└── Field: ").append(fieldName);
        return sb.toString();
    }
}