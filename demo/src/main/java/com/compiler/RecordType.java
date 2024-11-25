package com.compiler;

import java.util.Map;
import java.util.HashMap;
import java.util.List;

public class RecordType extends Type {
    private final Map<String, Type> fields;

    public RecordType(String name, Map<String, Type> fields) {
        super("record " + name);
        this.fields = fields;
    }

    // Constructor for creating from list of variable declarations
    public RecordType(List<VariableDeclaration> fields) {
        super("record");
        this.fields = new HashMap<>();
        for (VariableDeclaration field : fields) {
            this.fields.put(field.getName(), field.getType());
        }
    }

    public Map<String, Type> getFields() {
        return fields;
    }

    public Type getFieldType(String fieldName) {
        return fields.get(fieldName);
    }

    public boolean hasField(String fieldName) {
        return fields.containsKey(fieldName);
    }

    // Make fields iterable
    public Iterable<Map.Entry<String, Type>> getFieldEntries() {
        return fields.entrySet();
    }
}
