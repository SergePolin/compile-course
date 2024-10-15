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
        return "RoutineCallExpression(" + name + ", arguments=" + arguments + ")";
    }
}
