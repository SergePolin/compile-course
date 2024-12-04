package com.compiler;

/**
 * Represents a variable expression in the abstract syntax tree.
 * A variable expression consists of a variable name that can be evaluated
 * to retrieve its value from the execution context.
 */
public class VariableExpression extends Expression {
    /** The name of the variable */
    private String varName;

    /**
     * Constructs a new variable expression with the given variable name.
     *
     * @param varName the name of the variable
     */
    public VariableExpression(String varName) {
        this.varName = varName;
    }

    /**
     * Returns a string representation of this variable expression in AST format.
     *
     * @return AST string representation
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("VariableExpression\n");
        sb.append("└── Variable: '").append(varName).append("'");
        return sb.toString();
    }

    /**
     * Evaluates this variable expression by looking up its value.
     * Currently returns null as implementation is pending.
     *
     * @return the value of the variable, or null if not implemented
     */
    public Object evaluate() {
        return null;
    }
}
