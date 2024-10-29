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
        sb.append("RoutineDecl(")
                .append("name=").append(name)
                .append(", parameters=[");

        if (parameters != null) {
            for (int i = 0; i < parameters.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(parameters.get(i));
            }
        }

        sb.append("]");
        if (returnType != null) {
            sb.append(", returnType=").append(returnType);
        }
        sb.append(", body=[");

        if (body != null) {
            for (int i = 0; i < body.size(); i++) {
                if (i > 0)
                    sb.append(", ");
                sb.append(body.get(i));
            }
        }

        sb.append("])");
        return sb.toString();
    }
}