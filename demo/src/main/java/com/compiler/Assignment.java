package com.compiler;

public class Assignment extends Statement {
    private String target;
    private Expression value;
    private Expression index;  // For array assignments

    // Constructor for regular assignments
    public Assignment(String target, Expression value) {
        this.target = target;
        this.value = value;
        this.index = null;
    }

    // Constructor for array assignments
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
     public Expression getIndex() {
        return index;
    }

    @Override
    public String toString() {
        if (index != null) {
            return target + "[" + index + "] := " + value;
        }
        return target + " := " + value;
    }
}
