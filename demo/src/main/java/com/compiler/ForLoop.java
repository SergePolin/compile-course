package com.compiler;

import java.util.List;

public class ForLoop extends Statement {
    private String variable;
    private Reverse reverse;
    private Expression start;
    private Expression end;
    private List<Statement> body;

    public ForLoop(String variable, Reverse reverse, Expression start, Expression end, List<Statement> body) {
        this.variable = variable;
        this.reverse = reverse;
        this.start = start;
        this.end = end;
        this.body = body;
    }

    public String getVariable() {
        return variable;
    }

    public List<Statement> getBody() {
        return body;
    }

    public String getIteratorName() {
        return variable;
    }

    public Expression getRangeStart() {
        return start;
    }

    public Expression getRangeEnd() {
        return end;
    }

    public void execute() {
        int startVal = ((Integer) start.evaluate());
        int endVal = ((Integer) end.evaluate());

        if (reverse.isReverse()) {
            for (int i = endVal; i >= startVal; i--) {
                // TODO: Set variable value to i in symbol table
                for (Statement stmt : body) {
                    // Execute each statement in the loop body
                    if (stmt instanceof PrintStatement) {
                        ((PrintStatement) stmt).execute();
                    }
                    // Add other statement type executions as needed
                }
            }
        } else {
            for (int i = startVal; i <= endVal; i++) {
                // TODO: Set variable value to i in symbol table
                for (Statement stmt : body) {
                    // Execute each statement in the loop body
                    if (stmt instanceof PrintStatement) {
                        ((PrintStatement) stmt).execute();
                    }
                    // Add other statement type executions as needed
                }
            }
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("ForLoop {\n");
        sb.append("  variable: ").append(variable).append("\n");
        sb.append("  reverse: ").append(reverse.isReverse()).append("\n");
        sb.append("  start: ").append(start).append("\n");
        sb.append("  end: ").append(end).append("\n");
        sb.append("  body: [\n");
        for (Statement stmt : body) {
            String[] lines = stmt.toString().split("\n");
            for (String line : lines) {
                sb.append("    ").append(line).append("\n");
            }
        }
        sb.append("  ]\n");
        sb.append("}");
        return sb.toString();
    }
}
