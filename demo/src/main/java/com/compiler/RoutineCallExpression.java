package com.compiler;

import java.util.List;

public class RoutineCallExpression extends Expression {
    private String name;
    private List<Expression> arguments;

    public RoutineCallExpression(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public Object evaluate() {
        // Logic to evaluate the routine call as an expression
        return null; // Modify as per the logic
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoutineCall\n");
        sb.append("├── Name: ").append(name).append("\n");
        sb.append("└── Arguments:\n");
        for (int i = 0; i < arguments.size(); i++) {
            Expression arg = arguments.get(i);
            if (i == arguments.size() - 1) {
                sb.append("    └── ").append(arg.toString().replace("\n", "\n    "));
            } else {
                sb.append("    ├── ").append(arg.toString().replace("\n", "\n    "));
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
