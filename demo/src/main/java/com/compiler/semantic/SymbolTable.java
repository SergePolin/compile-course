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

/**
 * The SymbolTable class manages variable, routine and type declarations during
 * semantic analysis.
 * It implements scope-based symbol management using a stack of hash maps.
 */
public class SymbolTable {
    // Stack of scopes, where each scope is a map of variable names to their types
    private List<Map<String, Type>> scopes;

    // Map of routine names to their declarations
    private Map<String, RoutineDecl> routines;

    // Map of user-defined type names to their type definitions
    private Map<String, Type> types;

    // Set of built-in type names like "integer", "boolean", etc.
    private Set<String> builtInTypes;

    /**
     * Creates a new symbol table with an initial global scope
     */
    public SymbolTable() {
        this.scopes = new ArrayList<>();
        this.scopes.add(new HashMap<>());
        this.routines = new HashMap<>();
        this.types = new HashMap<>();
        this.builtInTypes = new HashSet<>();
        initializeBuiltInTypes();
    }

    /**
     * Initializes the set of built-in primitive types
     */
    private void initializeBuiltInTypes() {
        builtInTypes.add("integer");
        builtInTypes.add("real");
        builtInTypes.add("boolean");
        builtInTypes.add("string");
        builtInTypes.add("void");
    }

    /**
     * Creates a new scope for block-level declarations
     */
    public void enterScope() {
        scopes.add(new HashMap<>());
    }

    /**
     * Exits the current scope, removing all its declarations
     */
    public void exitScope() {
        if (scopes.size() > 1) { // Preserve global scope
            scopes.remove(scopes.size() - 1);
        }
    }

    /**
     * Declares a new variable in the current scope
     */
    public void declareVariable(String name, Type type) {
        scopes.get(scopes.size() - 1).put(name, type);
    }

    /**
     * Checks if a variable is defined in any scope
     */
    public boolean isDefined(String name) {
        // Search scopes from innermost to outermost
        for (int i = scopes.size() - 1; i >= 0; i--) {
            if (scopes.get(i).containsKey(name)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if a variable is defined in the current scope only
     */
    public boolean isDefinedInCurrentScope(String name) {
        return scopes.get(scopes.size() - 1).containsKey(name);
    }

    /**
     * Gets the type of a variable by searching all scopes
     */
    public Type getType(String name) {
        // Search scopes from innermost to outermost
        for (int i = scopes.size() - 1; i >= 0; i--) {
            Type type = scopes.get(i).get(name);
            if (type != null) {
                return type;
            }
        }
        return null;
    }

    /**
     * Declares a new routine with its name and declaration
     */
    public void declareRoutine(String name, RoutineDecl routine) {
        routines.put(name, routine);
    }

    /**
     * Gets a routine's declaration by name
     */
    public RoutineDecl getRoutine(String name) {
        return routines.get(name);
    }

    /**
     * Declares a new user-defined type
     */
    public void declareType(String name, Type type) {
        types.put(name, type);
    }

    /**
     * Checks if a type is defined (either built-in or user-defined)
     */
    public boolean isTypeDefined(Type type) {
        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            return builtInTypes.contains(typeName) || types.containsKey(typeName);
        }
        return types.containsValue(type);
    }

    /**
     * Resets the symbol table to its initial state
     */
    public void clear() {
        scopes.clear();
        scopes.add(new HashMap<>());
        routines.clear();
        types.clear();
        initializeBuiltInTypes();
    }
}