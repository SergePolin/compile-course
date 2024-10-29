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
        return "RoutineCall(" + name + ", arguments=" + arguments + ")";
    }
}
