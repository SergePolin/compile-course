package com.compiler;

import java.util.List;

public class RoutineCallStatement extends Statement {
    private String name;
    private List<Expression> arguments;

    public RoutineCallStatement(String name, List<Expression> arguments) {
        this.name = name;
        this.arguments = arguments;
    }

    @Override
    public String toString() {
        return "RoutineCallStatement(" + name + ", arguments=" + arguments + ")";
    }
}
