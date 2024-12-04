package com.compiler.semantic;

/**
 * Represents a semantic error detected during semantic analysis of the program.
 * Semantic errors include type mismatches, undefined variables, invalid operations, etc.
 */
public class SemanticError {
    /** The error message describing the semantic issue */
    private String message;

    /**
     * Creates a new semantic error with the specified error message.
     * @param message The error message describing the semantic issue
     */
    public SemanticError(String message) {
        this.message = message;
    }

    /**
     * Returns a string representation of this semantic error.
     * @return A string in the format "SemanticError: [error message]"
     */
    @Override
    public String toString() {
        return "SemanticError: " + message;
    }
}