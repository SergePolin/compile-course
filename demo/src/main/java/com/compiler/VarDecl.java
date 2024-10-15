package com.compiler;

public class VarDecl extends Statement {
    private String varName;
    private String type;
    private Expression value;
    private Integer size;

    public VarDecl(String varName, String type, Expression value) {
        this.varName = varName;
        this.type = type;
        this.value = value;
        this.size = null;

        if (type != null && type.startsWith("array[")) {
            int endIndex = type.indexOf(']');
            if (endIndex != -1) {
                String sizeStr = type.substring(6, endIndex);
                this.size = Integer.parseInt(sizeStr);
                this.type = type.substring(endIndex + 2); // Get the base type after "] "
            }
        }
    }

    @Override
    public String toString() {
        return "VarDecl(\n" +
                "  varName: " + varName + ",\n" +
                "  type: " + type + ",\n" +
                "  value: " + value + "\n" +
                ")";
    }
}
