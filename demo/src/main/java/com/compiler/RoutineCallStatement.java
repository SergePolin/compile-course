package com.compiler;

import java.util.List;

public class RoutineCallStatement extends Statement {
    private String name;
    private List<Expression> arguments;

    public RoutineCallStatement(String name, List<Expression> arguments) {
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
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoutineCallStatement\n");
        sb.append("├── name: ").append(name).append("\n");
        sb.append("└── arguments:\n");
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
        return sb.toString();
    }
}
