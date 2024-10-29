package com.compiler;

import java.util.List;

public class WhileStatement extends Statement {
    private Expression condition;
    private List<Statement> body;

    public WhileStatement(Expression condition, List<Statement> body) {
        this.condition = condition;
        this.body = body;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getBody() {
        return body;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("WhileStatement\n");
        builder.append("├── Condition\n");
        builder.append("│   └── ").append(condition).append("\n");
        builder.append("└── Body\n");
        for (int i = 0; i < body.size(); i++) {
            Statement bodyStmt = body.get(i);
            String prefix = (i == body.size() - 1) ? "    └── " : "    ├── ";
            builder.append(prefix).append(bodyStmt.toString().replace("\n", "\n    │   ")).append("\n");
        }
        return builder.toString();
    }
}