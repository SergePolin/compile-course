package com.compiler;

import java.util.List;

public class Program {
    private List<Statement> statements;

    public Program(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Program {\n");
        for (Statement stmt : statements) {
            String[] lines = stmt.toString().split("\n");
            for (String line : lines) {
                sb.append("  ").append(line).append("\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }
}
