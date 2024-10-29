package com.compiler;

import java.util.List;

public class RoutineCall extends Expression {
    private String name;
    private List<Expression> arguments;

    public RoutineCall(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public Object evaluate() {
        // TODO: Implement routine call
        throw new UnsupportedOperationException("Routine call not implemented yet");
    }

    @Override
    public String toString() {
        return "RoutineCall(" + name + ", arguments=" + arguments + ")";
    }
}
