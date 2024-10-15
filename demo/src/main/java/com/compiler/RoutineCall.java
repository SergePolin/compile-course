package com.compiler;

import java.util.List;

public class RoutineCall extends Statement {
    private String routineName;
    private List<Expression> arguments;

    public RoutineCall(String routineName, List<Expression> arguments) {
        this.routineName = routineName;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("RoutineCall {\n");
        builder.append("    routineName: ").append(routineName).append("\n");
        builder.append("    arguments: [\n");
        for (int i = 0; i < arguments.size(); i++) {
            builder.append("        ").append(arguments.get(i).toString().replace("\n", "\n        "));
            if (i < arguments.size() - 1) {
                builder.append(",\n");
            } else {
                builder.append("\n");
            }
        }
        builder.append("    ]\n");
        builder.append("}");
        return builder.toString();
    }
}
