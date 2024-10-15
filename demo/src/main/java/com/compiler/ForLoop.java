package com.compiler;

import java.util.List;

public class ForLoop extends Statement {
    private String variable;
    private Expression start;
    private Expression end;
    private List<Statement> body;
    private Reverse reverse;

    public ForLoop(String variable, Expression start, Expression end, List<Statement> body, Reverse reverse) {
        this.variable = variable;
        this.start = start;
        this.end = end;
        this.body = body;
        this.reverse = reverse;
    }

    public String getVariable() {
        return variable;
    }

    public Expression getStart() {
        return start;
    }

    public Expression getEnd() {
        return end;
    }

    public List<Statement> getBody() {
        return body;
    }

    public Reverse getReverse() {
        return reverse;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ForLoop(\n");
        sb.append("  variable: ").append(variable).append(",\n");
        sb.append("  reverse: ").append(reverse).append(",\n");
        sb.append("  start: ").append(start).append(",\n");
        sb.append("  end: ").append(end).append(",\n");
        sb.append("  body: [\n");
        for (Statement stmt : body) {
            sb.append("    ").append(stmt.toString().replace("\n", "\n    ")).append(",\n");
        }
        sb.append("  ]\n");
        sb.append(")");
        return sb.toString();
    }
}
