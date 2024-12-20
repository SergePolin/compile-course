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
 * This class is responsible for:
 * - Maintaining variable scope hierarchies
 * - Managing routine declarations and their signatures
 * - Tracking user-defined and built-in types
 * - Providing symbol resolution across nested scopes
 */
public class SymbolTable {
    /** Stack of scope levels, where each scope is a map of variable names to their types.
     * The first scope (index 0) is the global scope, and subsequent scopes represent nested blocks. */
    private List<Map<String, Type>> scopes;

    /** Map storing all routine declarations, keyed by routine name.
     * Used for validating routine calls and type checking parameters. */
    private Map<String, RoutineDecl> routines;

    /** Map of user-defined type names to their corresponding type definitions.
     * Includes record types, array types, and any other custom type declarations. */
    private Map<String, Type> types;

    /** Set containing the names of all built-in types supported by the language.
     * This includes primitive types like "integer", "boolean", "real", etc. */
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
     * @return false if routine is already defined, true otherwise
     */
    public boolean declareRoutine(String name, RoutineDecl routine) {
        if (routines.containsKey(name)) {
            return false;
        }
        routines.put(name, routine);
        return true;
    }

    /**
     * Gets a routine's declaration by name
     */
    public RoutineDecl getRoutine(String name) {
        return routines.get(name);
    }

    /**
     * Checks if a routine is defined
     */
    public boolean isRoutineDefined(String name) {
        return routines.containsKey(name);
    }

    /**
     * Defines a new type in the symbol table
     */
    public void defineType(String name, Type type) {
        types.put(name, type);
    }

    /**
     * Checks if a type name is defined
     */
    public boolean isTypeDefined(String typeName) {
        return builtInTypes.contains(typeName) || types.containsKey(typeName);
    }

    /**
     * Checks if a type is defined (either built-in or user-defined)
     */
    public boolean isTypeDefined(Type type) {
        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            return builtInTypes.contains(typeName) || types.containsKey(typeName);
        }
        return true; // Non-simple types (Record, Array) are always considered valid
    }

    /**
     * Resets the symbol table to its initial state
     */
    public void clear() {
        scopes.clear();
        scopes.add(new HashMap<>());
        routines.clear();  // Clear routines map
        types.clear();
        initializeBuiltInTypes();
    }

    /**
     * Gets the actual type definition for a type name
     */
    public Type getTypeDefinition(String typeName) {
        return types.get(typeName);
    }

    public boolean declare(String name, Type type) {
        if (isDefinedInCurrentScope(name)) {
            return false;
        }
        scopes.get(scopes.size() - 1).put(name, type);
        return true;
    }
}