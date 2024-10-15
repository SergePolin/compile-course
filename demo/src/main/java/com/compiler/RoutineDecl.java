package com.compiler;

import java.util.List;

public class RoutineDecl extends Statement {
    private String name;
    private List<Parameter> parameters;
    private String returnType;
    private List<Statement> body;

    public RoutineDecl(String name, List<Parameter> parameters, String returnType, List<Statement> body) {
        this.name = name;
        this.parameters = parameters;
        this.returnType = returnType;
        this.body = body;
    }

    public RoutineDecl(String name, List<Statement> body) {
        this.name = name;
        this.parameters = null;
        this.returnType = null;
        this.body = body;
    }

    public String getName() {
        return name;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public String getReturnType() {
        return returnType;
    }

    public List<Statement> getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "RoutineDecl(" + name + ", parameters=" + parameters + ", returnType=" + returnType + ", body=" + body
                + ")";
    }
}