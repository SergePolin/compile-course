package com.compiler;

import java.util.List;

/**
 * Represents a routine (function/procedure) call statement in the program.
 * A routine call statement consists of a routine name and a list of argument expressions.
 * Unlike RoutineCall, this represents a standalone statement rather than an expression.
 */
public class RoutineCallStatement extends Statement {
    /** The name of the routine being called */
    private String name;
    
    /** The list of argument expressions passed to the routine */
    private List<Expression> arguments;

    /**
     * Creates a new routine call statement.
     * 
     * @param name The name of the routine to call
     * @param arguments The list of argument expressions to pass to the routine
     */
    public RoutineCallStatement(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    /**
     * Gets the name of the routine being called.
     * 
     * @return The routine name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the list of argument expressions.
     * 
     * @return The list of arguments passed to this routine call
     */
    public List<Expression> getArguments() {
        return arguments;
    }

    /**
     * Returns a string representation of this routine call statement in a tree format.
     * 
     * @return A formatted string showing the routine call statement structure
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Routine Call Statement\n");
        sb.append("├── Name: ").append(name).append("\n");
        if (arguments.isEmpty()) {
            sb.append("└── Arguments: <none>\n");
        } else {
            sb.append("└── Arguments:\n");
            for (int i = 0; i < arguments.size(); i++) {
                Expression arg = arguments.get(i);
                String[] lines = arg.toString().split("\n");
                for (int j = 0; j < lines.length; j++) {
                    if (i == arguments.size() - 1) {
                        sb.append(j == 0 ? "    └── " : "        ").append(lines[j]).append("\n");
                    } else {
                        sb.append(j == 0 ? "    ├── " : "    │   ").append(lines[j]).append("\n");
                    }
                }
            }
        }
        return sb.toString();
    }
}
