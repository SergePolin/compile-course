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

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return returnType;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("RoutineDecl\n");
        sb.append("├── name: ").append(name).append("\n");

        // Parameters
        if (parameters != null && !parameters.isEmpty()) {
            sb.append("├── parameters:\n");
            for (int i = 0; i < parameters.size(); i++) {
                String prefix = (i == parameters.size() - 1) ? "│   └── " : "│   ├── ";
                sb.append(prefix).append(parameters.get(i).toString().replace("\n", "\n│   │   ")).append("\n");
            }
        }

        // Return type
        if (returnType != null) {
            sb.append("├── returnType: ").append(returnType.toString().replace("\n", "\n│   ")).append("\n");
        }

        // Body
        if (body != null && !body.isEmpty()) {
            sb.append("└── body:\n");
            for (int i = 0; i < body.size(); i++) {
                String prefix = (i == body.size() - 1) ? "    └── " : "    ├── ";
                String[] lines = body.get(i).toString().split("\n");
                sb.append(prefix).append(lines[0]).append("\n");
                for (int j = 1; j < lines.length; j++) {
                    sb.append("    │   ").append(lines[j]).append("\n");
                }
            }
        }

        return sb.toString();
    }
}