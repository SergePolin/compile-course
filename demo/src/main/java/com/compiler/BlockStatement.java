package com.compiler;

import java.util.List;

public class BlockStatement extends Statement {
    private List<Statement> statements;

    public BlockStatement(List<Statement> statements) {
        this.statements = statements;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("BlockStatement(\n");
        sb.append("  statements: [\n");
        for (Statement stmt : statements) {
            sb.append("    ").append(stmt.toString().replace("\n", "\n    ")).append(",\n");
        }
        sb.append("  ]\n");
        sb.append(")");
        return sb.toString();
    }
}
