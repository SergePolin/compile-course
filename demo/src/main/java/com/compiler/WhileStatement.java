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
        StringBuilder sb = new StringBuilder();
        sb.append("WhileStatement\n");
        sb.append("├── condition: ").append(condition.toString().replace("\n", "\n│   ")).append("\n");
        sb.append("└── body: ");
        if (body.isEmpty()) {
            sb.append("empty");
        } else {
            sb.append("\n");
            for (int i = 0; i < body.size(); i++) {
                Statement stmt = body.get(i);
                if (i == body.size() - 1) {
                    sb.append("    └── ").append(stmt.toString().replace("\n", "\n    "));
                } else {
                    sb.append("    ├── ").append(stmt.toString().replace("\n", "\n    │   "));
                    sb.append("\n");
                }
            }
        }
        return sb.toString();
    }
}