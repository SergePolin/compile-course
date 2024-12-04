package com.compiler;

/**
 * Represents a field assignment statement in a record.
 * This class handles assignments of values to fields within record types.
 */
public class FieldAssignment extends Statement {
    private final String recordName;
    private final String fieldName; 
    private final Expression value;
    private final Type type;

    /**
     * Constructs a new field assignment statement.
     *
     * @param recordName The name of the record containing the field
     * @param fieldName The name of the field being assigned to
     * @param value The expression value being assigned
     * @param type The type of the field
     */
    public FieldAssignment(String recordName, String fieldName, Expression value, Type type) {
        this.recordName = recordName;
        this.fieldName = fieldName;
        this.value = value;
        this.type = type;
    }

    /**
     * Gets the name of the record containing the field.
     *
     * @return The record name
     */
    public String getRecordName() {
        return recordName;
    }

    /**
     * Gets the name of the field being assigned to.
     *
     * @return The field name
     */
    public String getFieldName() {
        return fieldName;
    }

    /**
     * Gets the expression value being assigned to the field.
     *
     * @return The value expression
     */
    public Expression getValue() {
        return value;
    }

    /**
     * Gets the type of the field.
     *
     * @return The field type
     */
    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return String.format("%s.%s := %s [type: %s]", 
            recordName, fieldName, value, type);
    }
}