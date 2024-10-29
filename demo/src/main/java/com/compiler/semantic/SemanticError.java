package com.compiler.semantic;

public class SemanticError {
    private String message;

    public SemanticError(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return "SemanticError: " + message;
    }
}