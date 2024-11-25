package com.compiler;

import java.util.List;

public class BlockStatement extends Statement {
    private List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    public List<Statement> getStatements() {
        return statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BlockStatement\n");
        sb.append("└── statements\n");
        for (int i = 0; i < statements.size(); i++) {
            Statement stmt = statements.get(i);
            String prefix = (i == statements.size() - 1) ? "    └── " : "    ├── ";
            String[] lines = stmt.toString().split("\n");
            sb.append(prefix).append(lines[0]).append("\n");
            for (int j = 1; j < lines.length; j++) {
                String continuationPrefix = (i == statements.size() - 1) ? "        " : "    │   ";
                sb.append(continuationPrefix).append(lines[j]).append("\n");
            }
        }
        return sb.toString();
    }
}
