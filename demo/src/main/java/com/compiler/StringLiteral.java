package com.compiler;

public class StringLiteral extends Expression {
    private final String value;

    public StringLiteral(String value) {
        System.out.println("[DEBUG] Creating StringLiteral with raw value: " + value);
        this.value = value;
    }

    public String getValue() {
        if (value == null || value.length() < 2) {
            System.out.println("[DEBUG] Invalid string value: " + value);
            return "";
        }
        
        String unquoted = value.substring(1, value.length() - 1);
        System.out.println("[DEBUG] Unquoted string value: " + unquoted);
        
        String unescaped = unquoted
            .replace("\\\\", "\\")
            .replace("\\n", "\n")
            .replace("\\t", "\t")
            .replace("\\\"", "\"")
            .replace("\\r", "\r");
            
        System.out.println("[DEBUG] Final string value: " + unescaped);
        return unescaped;
    }

    @Override
    public Object evaluate() {
        return getValue();
    }

    @Override
    public String toString() {
        return "StringLiteral(" + value + ")";
    }
}
