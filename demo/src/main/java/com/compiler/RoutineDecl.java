package com.compiler;

import java.util.List;

public class RoutineDecl extends Statement {
    private String name;
    private List<Parameter> parameters;
    private Type returnType;
    private List<Statement> body;

    public RoutineDecl(String name, List<Parameter> parameters, Type returnType, List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    public RoutineDecl(String name, List<Statement> body) {
        this.name = name;
        this.parameters = List.of();
        this.returnType = null;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<Statement> getBody() {
        return body;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public Type getReturnType() {
        return returnType;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoutineDecl(").append(name).append(")\n");
        for (int i = 0; i < body.size(); i++) {
            String[] lines = body.get(i).toString().split("\n");
            if (i == body.size() - 1) {
                sb.append("└── ").append(lines[0]).append("\n");
                for (int j = 1; j < lines.length; j++) {
                    sb.append("    ").append(lines[j]).append("\n");
                }
            } else {
                sb.append("├── ").append(lines[0]).append("\n");
                for (int j = 1; j < lines.length; j++) {
                    sb.append("│   ").append(lines[j]).append("\n");
                }
            }
        }
        return sb.toString();
    }
}