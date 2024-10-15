package com.compiler;

public class Reverse {
    private boolean isReverse;

    public Reverse(boolean isReverse) {
        this.isReverse = isReverse;
    }

    public boolean isReverse() {
        return isReverse;
    }

    @Override
    public String toString() {
        return "Reverse{" +
                "isReverse=" + isReverse +
                '}';
    }
}