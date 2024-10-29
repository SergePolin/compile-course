package com.compiler.semantic;

import com.compiler.Type;
import com.compiler.SimpleType;
import com.compiler.RoutineDecl;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;

public class SymbolTable {
    private List<Map<String, Type>> scopes;
    private Map<String, RoutineDecl> routines;
    private Map<String, Type> types;
    private Set<String> builtInTypes;

    public SymbolTable() {
        this.scopes = new ArrayList<>();
        this.scopes.add(new HashMap<>()); // Global scope
        this.routines = new HashMap<>();
        this.types = new HashMap<>();
        this.builtInTypes = new HashSet<>();
        initializeBuiltInTypes();
    }

    private void initializeBuiltInTypes() {
        builtInTypes.add("integer");
        builtInTypes.add("real");
        builtInTypes.add("boolean");
        builtInTypes.add("string");
        builtInTypes.add("void");
    }

    public void enterScope() {
        scopes.add(new HashMap<>());
    }

    public void exitScope() {
        if (scopes.size() > 1) { // Don't remove global scope
            scopes.remove(scopes.size() - 1);
        }
    }

    public void declareVariable(String name, Type type) {
        scopes.get(scopes.size() - 1).put(name, type);
    }

    public boolean isDefined(String name) {
        // Check all scopes from innermost to outermost
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    public boolean isDefinedInCurrentScope(String name) {
        return scopes.get(scopes.size() - 1).containsKey(name);
    }

    public Type getType(String name) {
        // Check all scopes from innermost to outermost
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Type type = scopes.get(i).get(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    public void declareRoutine(String name, RoutineDecl routine) {
        routines.put(name, routine);
    }

    public RoutineDecl getRoutine(String name) {
        return routines.get(name);
    }

    public void declareType(String name, Type type) {
        types.put(name, type);
    }

    public boolean isTypeDefined(Type type) {
        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            return builtInTypes.contains(typeName) || types.containsKey(typeName);
        }
        return types.containsValue(type);
    }

    public void clear() {
        scopes.clear();
        scopes.add(new HashMap<>()); // Reset to just global scope
        routines.clear();
        types.clear();
        initializeBuiltInTypes();
    }
}