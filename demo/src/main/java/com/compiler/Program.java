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
        StringBuilder builder = new StringBuilder();
        builder.append("Program\n");
        for (int i = 0; i < statements.size(); i++) {
            String prefix = (i == statements.size() - 1) ? "└── " : "├── ";
            builder.append(prefix).append(statements.get(i).toString().replace("\n", "\n    "));
            if (i < statements.size() - 1) {
                builder.append("\n");
            }
        }
        return builder.toString();
    }
}
