package com.compiler;

public class ReadStatement extends Statement {
    private String identifier;

    public ReadStatement(String identifier) {
        this.identifier = identifier;
    }

    public String getVariable() {
        return identifier;
    }

    @Override
    public String toString() {
        return "Read\n" +
                "└── " + identifier;
    }
}
