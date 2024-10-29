package com.compiler;

import java.util.List;

public class IfStatement extends Statement {
    private Expression condition;
    private List<Statement> thenBody;
    private List<Statement> elseBody;

    public IfStatement(Expression condition, List<Statement> thenBody, List<Statement> elseBody) {
        this.condition = condition;
        this.thenBody = thenBody;
        this.elseBody = elseBody;
    }

    public Expression getCondition() {
        return condition;
    }

    public List<Statement> getThenStatements() {
        return thenBody;
    }

    public List<Statement> getElseStatements() {
        return elseBody;
    }

    public void setCondition(Expression condition) {
        this.condition = condition;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("IfStatement(")
                .append("condition=").append(condition)
                .append(", thenBody=").append(thenBody);
        if (elseBody != null && !elseBody.isEmpty()) {
            sb.append(", elseBody=").append(elseBody);
        }
        sb.append(")");
        return sb.toString();
    }
}
