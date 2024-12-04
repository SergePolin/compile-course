package com.compiler;

/**
 * Abstract base class for all expression nodes in the Abstract Syntax Tree (AST).
 * This class represents any construct that can be evaluated to produce a value,
 * including literals, variables, arithmetic operations, function calls, etc.
 */
public abstract class Expression {
    /**
     * Evaluates the expression and returns its value.
     * The actual type of the returned value depends on the specific expression type.
     *
     * @return The result of evaluating this expression, which could be a number,
     *         boolean, string, or other type depending on the expression
     */
    public abstract Object evaluate();

    /**
     * Returns a string representation of this expression.
     * Useful for debugging and error reporting.
     *
     * @return A string representation of the expression
     */
    @Override
    public abstract String toString();
}
