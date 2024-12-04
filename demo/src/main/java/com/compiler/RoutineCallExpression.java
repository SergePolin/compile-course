package com.compiler;

import java.util.List;

/**
 * Represents a routine (function/procedure) call expression in the program.
 * A routine call consists of a routine name and a list of argument expressions.
 */
public class RoutineCallExpression extends Expression {
    /** The name of the routine being called */
    private String name;
    
    /** The list of argument expressions passed to the routine */
    private List<Expression> arguments;

    /**
     * Creates a new routine call expression.
     * 
     * @param name The name of the routine to call
     * @param arguments The list of argument expressions to pass to the routine
     */
    public RoutineCallExpression(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * Evaluates this routine call expression.
     * 
     * @return The result of executing the routine with the given arguments
     */
    @Override
    public Object evaluate() {
        // Logic to evaluate the routine call as an expression
        return null; // Modify as per the logic
    }

    /**
     * Returns a string representation of this routine call in a tree format.
     * 
     * @return A formatted string showing the routine call structure
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoutineCall Expression\n");
        sb.append("├── Name: ").append(name).append("\n");
        if (arguments.isEmpty()) {
            sb.append("└── Arguments: <none>");
        } else {
            sb.append("└── Arguments:\n");
            for (int i = 0; i < arguments.size(); i++) {
                Expression arg = arguments.get(i);
                String argStr = arg.toString().replace("\n", "\n    ");
                if (i == arguments.size() - 1) {
                    sb.append("    └── ").append(argStr);
                } else {
                    sb.append("    ├── ").append(argStr).append("\n");
                }
            }
        }
        return sb.toString();
    }
}
