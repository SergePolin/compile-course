package com.compiler;

public class Assignment extends Statement {
    private String target;
    private Expression value;
    private Expression index; // for array assignments

    public Assignment(String target, Expression value) {
        this.target = target;
        this.value = value;
        this.index = null;
    }

    public Assignment(String target, Expression index, Expression value) {
        this.target = target;
        this.index = index;
        this.value = value;
    }

    public String getTarget() {
        return target;
    }

    public Expression getValue() {
        return value;
    }

    public void setValue(Expression value) {
        this.value = value;
    }

    @Override
    public String toString() {
        if (index != null) {
            return "Assignment(" + target + "[" + index + "] := " + value + ")";
        }
        return "Assignment(" + target + " := " + value + ")";
    }
}
