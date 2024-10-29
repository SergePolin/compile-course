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
        sb.append("Program\n");
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            String[] lines = stmt.toString().split("\n");
            if (i == statements.size() - 1) {
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
