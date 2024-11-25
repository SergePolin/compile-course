package com.compiler;

public abstract class Statement {
    @Override
    public abstract String toString();

    public void execute() {
        // Default implementation
        throw new UnsupportedOperationException("Execute not implemented for " + getClass().getSimpleName());
    }
}
