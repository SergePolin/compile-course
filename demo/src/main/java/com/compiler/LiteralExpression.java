package com.compiler;

/**
 * Represents a literal value in the abstract syntax tree.
 * This class handles literal values of any type that appear in the source code.
 */
public class LiteralExpression extends Expression {
    /** The value of this literal */
    private Object value;

    /**
     * Constructs a new literal expression with the specified value.
     *
     * @param value The value of this literal
     */
    public LiteralExpression(Object value) {
        this.value = value;
    }

    /**
     * Returns a string representation of this literal expression using ASCII art tree structure.
     * The output includes the literal value in a hierarchical format.
     *
     * @return A formatted string representation of the literal expression
     */
    @Override
    public String toString() {
        String valueStr = value != null ? value.toString() : "null";
        return "LiteralExpression\n" +
               "└── " + valueStr;
    }

    /**
     * Evaluates this expression by returning the literal value.
     *
     * @return The literal value
     */
    public Object evaluate() {
        return value;
    }
}
