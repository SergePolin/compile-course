package com.compiler;

import java.util.List;

public class IfStatement extends Statement {
    private Expression condition;
    private List<Statement> thenStatements;
    private List<Statement> elseStatements; // This now includes ELSE IF clauses and the final ELSE

    public IfStatement(Expression condition, List<Statement> thenStatements, List<Statement> elseStatements) {
        this.condition = condition;
        this.thenStatements = thenStatements;
        this.elseStatements = elseStatements;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("IfStatement {\n");
        builder.append("    condition: ").append(condition).append("\n");
        builder.append("    thenStatements: [\n");
        for (Statement stmt : thenStatements) {
            builder.append("        ").append(stmt.toString().replace("\n", "\n        ")).append("\n");
        }
        builder.append("    ]\n");
        if (elseStatements != null && !elseStatements.isEmpty()) {
            builder.append("    elseStatements: [\n");
            for (Statement stmt : elseStatements) {
                builder.append("        ").append(stmt.toString().replace("\n", "\n        ")).append("\n");
            }
            builder.append("    ]\n");
        }
        builder.append("}");
        return builder.toString();
    }
}
