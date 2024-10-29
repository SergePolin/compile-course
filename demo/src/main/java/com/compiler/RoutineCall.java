package com.compiler;

import java.util.List;

public class RoutineCall extends Expression {
    private String name;
    private List<Expression> arguments;

    public RoutineCall(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    public String getName() {
        return name;
    }

    public List<Expression> getArguments() {
        return arguments;
    }

    @Override
    public Object evaluate() {
        // TODO: Implement routine call evaluation
        throw new UnsupportedOperationException("Routine call evaluation not implemented yet");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoutineCall\n");
        sb.append("├── name: ").append(name).append("\n");
        sb.append("└── arguments:\n");
        for (int i = 0; i < arguments.size(); i++) {
            Expression arg = arguments.get(i);
            if (i == arguments.size() - 1) {
                sb.append("    └── ").append(arg).append("\n");
            } else {
                sb.append("    ├── ").append(arg).append("\n");
            }
        }
        return sb.toString();
    }
}
