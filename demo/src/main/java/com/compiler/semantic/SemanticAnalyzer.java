package com.compiler.semantic;

import com.compiler.*;
import java.util.*;

/**
 * The SemanticAnalyzer performs semantic analysis on the AST to check for:
 * - Type checking
 * - Variable declarations and scoping
 * - Function/routine declarations and calls
 * - Control flow analysis
 * - Constant expression optimization
 * - Unused variable detection
 */
public class SemanticAnalyzer {
    // Symbol table to track variables and their types across scopes
    private SymbolTable symbolTable;

    // List to collect semantic errors found during analysis
    private List<SemanticError> errors;

    // Stack to track expected return types when analyzing routines
    private Stack<Type> expectedReturnTypes;

    // Set to track which variables are actually used
    private Set<String> usedVariables;

    // Flags to track context during analysis
    private boolean insideLoop; // Whether we're analyzing code inside a loop
    private boolean insideRoutine; // Whether we're analyzing code inside a routine

    /**
     * Creates a new semantic analyzer with empty state
     */
    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.expectedReturnTypes = new Stack<>();
        this.usedVariables = new HashSet<>();
        this.insideLoop = false;
        this.insideRoutine = false;
    }

    /**
     * Main entry point to analyze a program AST.
     * Performs semantic analysis and returns list of any errors found.
     */
    public List<SemanticError> analyze(Program program) {
        errors.clear();
        symbolTable.clear();
        usedVariables.clear();
        visit(program);
        optimizeConstantExpressions(program);
        removeUnusedVariables(program);
        return errors;
    }

    /**
     * Visits each statement in the program to perform semantic analysis
     */
    private void visit(Program program) {
        // First pass: collect all routine declarations
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl) {
                RoutineDecl routine = (RoutineDecl) stmt;
                symbolTable.declareRoutine(routine.getName(), routine);
            }
        }

        // Second pass: analyze routine bodies and other statements
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl) {
                visitRoutineDecl((RoutineDecl) stmt);
            } else {
                visitStatement(stmt);
            }
        }
    }

    /**
     * Dispatches to appropriate visit method based on statement type.
     * Each visit method performs semantic analysis specific to that type of
     * statement.
     */
    private void visitStatement(Statement stmt) {
        if (stmt instanceof VarDecl) {
            visitVarDecl((VarDecl) stmt);
        } else if (stmt instanceof Assignment) {
            visitAssignment((Assignment) stmt);
        } else if (stmt instanceof IfStatement) {
            visitIfStatement((IfStatement) stmt);
        } else if (stmt instanceof WhileStatement) {
            boolean wasInsideLoop = insideLoop;
            insideLoop = true;
            visitWhileStatement((WhileStatement) stmt);
            insideLoop = wasInsideLoop;
        } else if (stmt instanceof ForLoop) {
            boolean wasInsideLoop = insideLoop;
            insideLoop = true;
            visitForLoop((ForLoop) stmt);
            insideLoop = wasInsideLoop;
        } else if (stmt instanceof ReturnStatement) {
            if (!insideRoutine) {
                errors.add(new SemanticError("Return statement can only appear inside a routine"));
            }
            visitReturnStatement((ReturnStatement) stmt);
        } else if (stmt instanceof PrintStatement) {
            visitPrintStatement((PrintStatement) stmt);
        } else if (stmt instanceof ReadStatement) {
            visitReadStatement((ReadStatement) stmt);
        } else if (stmt instanceof RoutineDecl) {
            boolean wasInsideRoutine = insideRoutine;
            insideRoutine = true;
            visitRoutineDecl((RoutineDecl) stmt);
            insideRoutine = wasInsideRoutine;
        }
    }

    /**
     * Checks variable declarations for:
     * - Duplicate declarations in same scope
     * - Valid variable type
     * - Type compatibility of initializer if present
     */
    private void visitVarDecl(VarDecl decl) {
        // Check if variable is already declared in current scope
        if (symbolTable.isDefinedInCurrentScope(decl.getName())) {
            errors.add(new SemanticError("Variable " + decl.getName() + " is already declared in this scope"));
            return;
        }

        // Check if type exists
        Type declaredType = decl.getType();
        if (!isValidType(declaredType)) {
            errors.add(new SemanticError("Unknown type " + declaredType));
            return;
        }

        // Check initialization type if present
        if (decl.getInitializer() != null) {
            Type initType = getExpressionType(decl.getInitializer());
            if (initType == null) {
                errors.add(new SemanticError("Invalid initializer expression for variable " + decl.getName()));
                return;
            }
            if (!isTypeCompatible(declaredType, initType)) {
                errors.add(new SemanticError("Type mismatch: cannot initialize variable of type " +
                        declaredType + " with value of type " + initType));
                return;
            }
        }

        symbolTable.declareVariable(decl.getName(), declaredType);
    }

    /**
     * Checks assignments for:
     * - Variable existence
     * - Type compatibility between variable and value
     */
    private void visitAssignment(Assignment assign) {
        // Check if variable exists
        if (!symbolTable.isDefined(assign.getTarget())) {
            errors.add(new SemanticError("Undefined variable " + assign.getTarget()));
            return;
        }

        Type targetType = symbolTable.getType(assign.getTarget());
        Type valueType = getExpressionType(assign.getValue());

        if (valueType == null) {
            errors.add(new SemanticError("Cannot assign null value to variable " + assign.getTarget()));
            return;
        }

        if (!isTypeCompatible(targetType, valueType)) {
            errors.add(new SemanticError("Type mismatch in assignment: cannot assign value of type " +
                    valueType + " to variable of type " + targetType));
        }

        // Mark variable as used
        usedVariables.add(assign.getTarget());
    }

    /**
     * Checks if statements for:
     * - Boolean condition
     * - Valid statements in then/else blocks
     */
    private void visitIfStatement(IfStatement ifStmt) {
        // Check if the condition is a boolean expression
        Type conditionType = getExpressionType(ifStmt.getCondition());
        if (!isBoolean(conditionType)) {
            errors.add(new SemanticError("If statement condition must be a boolean expression"));
        }

        // Visit the 'then' block
        for (Statement stmt : ifStmt.getThenStatements()) {
            visitStatement(stmt);
        }

        // Visit the 'else' block if present
        if (ifStmt.getElseStatements() != null) {
            for (Statement stmt : ifStmt.getElseStatements()) {
                visitStatement(stmt);
            }
        }
    }

    /**
     * Checks while loops for:
     * - Boolean condition
     * - Valid statements in loop body
     */
    private void visitWhileStatement(WhileStatement whileStmt) {
        // Check if the condition is a boolean expression
        Type conditionType = getExpressionType(whileStmt.getCondition());
        if (!isBoolean(conditionType)) {
            errors.add(new SemanticError("While statement condition must be a boolean expression"));
        }

        // Visit the body of the loop
        for (Statement stmt : whileStmt.getBody()) {
            visitStatement(stmt);
        }
    }

    /**
     * Checks for loops for:
     * - Valid loop variable
     * - Valid statements in loop body
     */
    private void visitForLoop(ForLoop forLoop) {
        // Check if the loop variable is declared
        if (!symbolTable.isDefined(forLoop.getVariable())) {
            errors.add(new SemanticError("Undefined loop variable " + forLoop.getVariable()));
        }

        // Visit the body of the loop
        for (Statement stmt : forLoop.getBody()) {
            visitStatement(stmt);
        }
    }

    /**
     * Checks return statements for:
     * - Being inside a routine
     * - Type compatibility with routine return type
     */
    private void visitReturnStatement(ReturnStatement returnStmt) {
        if (expectedReturnTypes.isEmpty()) {
            errors.add(new SemanticError("Return statement not allowed in this context"));
            return;
        }

        Type expectedType = expectedReturnTypes.peek();
        Type actualType = getExpressionType(returnStmt.getExpression());

        if (expectedType == null && actualType != null) {
            errors.add(new SemanticError("Unexpected return value in routine without return type"));
            return;
        }

        if (expectedType != null && actualType == null) {
            errors.add(new SemanticError("Missing return value for routine with return type " + expectedType));
            return;
        }

        if (!isTypeCompatible(expectedType, actualType)) {
            errors.add(new SemanticError("Return type mismatch: expected " + expectedType +
                    ", but got " + actualType));
        }
    }

    /**
     * Checks print statements for:
     * - Valid expression to print
     */
    private void visitPrintStatement(PrintStatement printStmt) {
        Expression expr = printStmt.getExpression();

        // Check if the expression is a variable reference
        if (expr instanceof VariableReference) {
            String varName = ((VariableReference) expr).getName();
            if (!symbolTable.isDefined(varName)) {
                errors.add(new SemanticError("Undefined variable '" + varName + "' in print statement"));
                return;
            }
        }

        // Check if the expression type is valid
        Type expressionType = getExpressionType(expr);
        if (!isValidType(expressionType)) {
            errors.add(new SemanticError("Invalid type for print statement"));
        }
    }

    /**
     * Checks read statements for:
     * - Valid variable to read into
     */
    private void visitReadStatement(ReadStatement readStmt) {
        // Check if the variable exists
        if (!symbolTable.isDefined(readStmt.getVariable())) {
            errors.add(new SemanticError("Undefined variable " + readStmt.getVariable()));
        }

        // Mark variable as used
        usedVariables.add(readStmt.getVariable());
    }

    /**
     * Checks routine declarations for:
     * - Valid parameter types
     * - Valid return type
     * - Return statement presence when needed
     * - Valid routine body
     */
    private void visitRoutineDecl(RoutineDecl routine) {
        // Create a new scope for the routine
        symbolTable.enterScope();

        // Set insideRoutine flag
        boolean wasInsideRoutine = insideRoutine;
        insideRoutine = true; // Set flag before analyzing routine body

        // Add parameters to the new scope
        if (routine.getParameters() != null) {
            for (Parameter param : routine.getParameters()) {
                // Check if parameter type exists
                if (!isValidType(param.getType())) {
                    errors.add(new SemanticError("Unknown parameter type " + param.getType() +
                            " in routine " + routine.getName()));
                }
                symbolTable.declareVariable(param.getName(), param.getType());
            }
        }

        // Check if return type exists
        if (routine.getReturnType() != null && !isValidType(routine.getReturnType())) {
            errors.add(new SemanticError("Unknown return type " + routine.getReturnType() +
                    " in routine " + routine.getName()));
        }

        // Push return type for checking return statements
        if (routine.getReturnType() != null) {
            expectedReturnTypes.push(routine.getReturnType());
        }

        // Visit the routine body
        for (Statement stmt : routine.getBody()) {
            visitStatement(stmt);
        }

        // Check if non-void routine has a return statement
        Type returnType = routine.getReturnType();
        if (returnType != null &&
                !(returnType instanceof SimpleType && ((SimpleType) returnType).getName().equals("void")) &&
                !hasReturnStatement(routine.getBody())) {
            errors.add(new SemanticError("Routine '" + routine.getName() + "' must return a value"));
        }

        // Pop return type if we pushed one
        if (routine.getReturnType() != null) {
            expectedReturnTypes.pop();
        }

        // Restore previous insideRoutine state
        insideRoutine = wasInsideRoutine;

        // Exit the routine's scope
        symbolTable.exitScope();
    }

    /**
     * Checks if a list of statements contains a return statement
     */
    private boolean hasReturnStatement(List<Statement> statements) {
        for (Statement stmt : statements) {
            if (stmt instanceof ReturnStatement) {
                return true;
            }
            if (stmt instanceof IfStatement) {
                IfStatement ifStmt = (IfStatement) stmt;
                if (hasReturnStatement(ifStmt.getThenStatements()) &&
                        (ifStmt.getElseStatements() == null || hasReturnStatement(ifStmt.getElseStatements()))) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Determines the type of an expression through recursive analysis
     */
    private Type getExpressionType(Expression expr) {
        if (expr == null) {
            return null;
        }

        if (expr instanceof TypeCast) {
            TypeCast cast = (TypeCast) expr;
            Type sourceType = getExpressionType(cast.getExpression());
            Type targetType = cast.getTargetType();

            // Check if cast is valid
            if (isValidCast(sourceType, targetType)) {
                return targetType;
            } else {
                errors.add(new SemanticError("Invalid type cast from " + sourceType + " to " + targetType));
                return null;
            }
        }

        if (expr instanceof RoutineCall) {
            return getRoutineCallType((RoutineCall) expr);
        }

        if (expr instanceof IntegerLiteral) {
            return new SimpleType("integer");
        } else if (expr instanceof RealLiteral) {
            return new SimpleType("real");
        } else if (expr instanceof BooleanLiteral) {
            return new SimpleType("boolean");
        } else if (expr instanceof StringLiteral) {
            return new SimpleType("string");
        } else if (expr instanceof VariableReference) {
            String varName = ((VariableReference) expr).getName();
            Type type = symbolTable.getType(varName);
            if (type == null) {
                errors.add(new SemanticError("Undefined variable '" + varName + "'"));
            }
            return type;
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            Type leftType = getExpressionType(binExpr.getLeft());
            Type rightType = getExpressionType(binExpr.getRight());
            String operator = binExpr.getOperator();

            // Comparison operators always return boolean
            if (operator.equals(">") || operator.equals("<") ||
                    operator.equals(">=") || operator.equals("<=") ||
                    operator.equals("=") || operator.equals("!=")) {
                // Check if operands are comparable (both numeric)
                if (isNumeric(leftType) && isNumeric(rightType)) {
                    return new SimpleType("boolean");
                }
                return new SimpleType("boolean"); // Return boolean type even if operands are invalid
            }

            // Logical operators require boolean operands and return boolean
            if (operator.equals("and") || operator.equals("or") || operator.equals("xor")) {
                if (!isBoolean(leftType) || !isBoolean(rightType)) {
                    errors.add(new SemanticError("Logical operators require boolean operands"));
                }
                return new SimpleType("boolean");
            }

            // For arithmetic operations
            if (leftType instanceof SimpleType && rightType instanceof SimpleType) {
                String leftName = ((SimpleType) leftType).getName();
                String rightName = ((SimpleType) rightType).getName();

                if (leftName.equals("integer") && rightName.equals("integer")) {
                    return new SimpleType("integer");
                }
                if ((leftName.equals("integer") || leftName.equals("real")) &&
                        (rightName.equals("integer") || rightName.equals("real"))) {
                    return new SimpleType("real");
                }
            }
            return leftType;
        } else if (expr instanceof UnaryExpression) {
            UnaryExpression unaryExpr = (UnaryExpression) expr;
            Type operandType = getExpressionType(unaryExpr.getExpression());

            if (unaryExpr.getOperator().equals("not")) {
                if (!isBoolean(operandType)) {
                    errors.add(new SemanticError("Not operator requires boolean operand"));
                }
                return new SimpleType("boolean");
            }

            return operandType;
        }
        return null;
    }

    /**
     * Checks if a type is numeric (integer or real)
     */
    private boolean isNumeric(Type type) {
        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            return typeName.equals("integer") || typeName.equals("real");
        }
        return false;
    }

    /**
     * Checks routine calls for:
     * - Routine existence
     * - Correct number of arguments
     * - Type compatibility of arguments
     */
    private Type getRoutineCallType(RoutineCall call) {
        RoutineDecl routine = symbolTable.getRoutine(call.getName());
        if (routine == null) {
            errors.add(new SemanticError("Undefined routine " + call.getName()));
            return null;
        }

        // Check argument count
        List<Parameter> params = routine.getParameters();
        int expectedCount = params != null ? params.size() : 0;
        int actualCount = call.getArguments() != null ? call.getArguments().size() : 0;

        if (expectedCount != actualCount) {
            errors.add(new SemanticError("Wrong number of arguments in call to " + call.getName() +
                    ": expected " + expectedCount + ", got " + actualCount));
            return null;
        }

        // Check argument types
        if (params != null) {
            for (int i = 0; i < params.size(); i++) {
                Type expectedType = params.get(i).getType();
                Type actualType = getExpressionType(call.getArguments().get(i));
                if (!isTypeCompatible(expectedType, actualType)) {
                    errors.add(new SemanticError("Type mismatch in argument " + (i + 1) +
                            " of call to " + call.getName() + ": expected " + expectedType +
                            ", got " + actualType));
                }
            }
        }

        return routine.getReturnType();
    }

    /**
     * Checks if a type is valid according to the symbol table
     */
    private boolean isValidType(Type type) {
        // Check if the type is valid (exists in the symbol table)
        return symbolTable.isTypeDefined(type);
    }

    /**
     * Checks if two types are compatible for assignment/comparison
     */
    private boolean isTypeCompatible(Type expected, Type actual) {
        if (expected == null || actual == null) {
            return false;
        }

        // Both types are SimpleType
        if (expected instanceof SimpleType && actual instanceof SimpleType) {
            String expectedName = ((SimpleType) expected).getName();
            String actualName = ((SimpleType) actual).getName();

            // Same type
            if (expectedName.equals(actualName)) {
                return true;
            }

            // Integer to Real conversion is allowed
            if (expectedName.equals("real") && actualName.equals("integer")) {
                return true;
            }

            // String concatenation only allows string to string
            if (expectedName.equals("string")) {
                return actualName.equals("string");
            }
        }

        return false;
    }

    /**
     * Checks if a type is boolean
     */
    private boolean isBoolean(Type type) {
        if (type instanceof SimpleType) {
            return ((SimpleType) type).getName().equals("boolean");
        }
        return false;
    }

    /**
     * Removes unused variable declarations from the program
     */
    private void removeUnusedVariables(Program program) {
        Iterator<Statement> iterator = program.getStatements().iterator();
        while (iterator.hasNext()) {
            Statement stmt = iterator.next();
            if (stmt instanceof VarDecl) {
                VarDecl varDecl = (VarDecl) stmt;
                if (!usedVariables.contains(varDecl.getName())) {
                    iterator.remove();
                }
            }
        }
    }

    /**
     * Optimizes constant expressions in the program
     */
    private void optimizeConstantExpressions(Program program) {
        for (Statement stmt : program.getStatements()) {
            optimizeStatement(stmt);
        }
    }

    /**
     * Optimizes constant expressions within a statement
     */
    private void optimizeStatement(Statement stmt) {
        if (stmt instanceof VarDecl) {
            VarDecl varDecl = (VarDecl) stmt;
            if (varDecl.getInitializer() != null) {
                Expression optimized = optimizeExpression(varDecl.getInitializer());
                if (optimized != null) {
                    varDecl.setInitializer(optimized);
                }
            }
        } else if (stmt instanceof Assignment) {
            Assignment assign = (Assignment) stmt;
            Expression optimized = optimizeExpression(assign.getValue());
            if (optimized != null) {
                assign.setValue(optimized);
            }
        } else if (stmt instanceof IfStatement) {
            IfStatement ifStmt = (IfStatement) stmt;
            Expression optimizedCond = optimizeExpression(ifStmt.getCondition());
            if (optimizedCond != null) {
                ifStmt.setCondition(optimizedCond);
            }
            for (Statement s : ifStmt.getThenStatements()) {
                optimizeStatement(s);
            }
            if (ifStmt.getElseStatements() != null) {
                for (Statement s : ifStmt.getElseStatements()) {
                    optimizeStatement(s);
                }
            }
        } else if (stmt instanceof WhileStatement) {
            WhileStatement whileStmt = (WhileStatement) stmt;
            Expression optimizedCond = optimizeExpression(whileStmt.getCondition());
            if (optimizedCond != null) {
                whileStmt.setCondition(optimizedCond);
            }
            for (Statement s : whileStmt.getBody()) {
                optimizeStatement(s);
            }
        }
    }

    /**
     * Attempts to optimize constant expressions
     */
    private Expression optimizeExpression(Expression expr) {
        if (expr == null)
            return null;

        // Try to evaluate constant expressions
        if (isConstantExpression(expr)) {
            try {
                Object value = evaluateConstant(expr);
                if (value instanceof Integer) {
                    return new IntegerLiteral((Integer) value);
                } else if (value instanceof Boolean) {
                    return new BooleanLiteral((Boolean) value);
                } else if (value instanceof Double) {
                    return new RealLiteral((Double) value);
                } else if (value instanceof String) {
                    return new StringLiteral((String) value);
                }
            } catch (Exception e) {
                // If evaluation fails, return original expression
                return expr;
            }
        }

        // Recursively optimize binary expressions
        if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            Expression leftOpt = optimizeExpression(binExpr.getLeft());
            Expression rightOpt = optimizeExpression(binExpr.getRight());
            if (leftOpt != null || rightOpt != null) {
                return new BinaryExpression(
                        leftOpt != null ? leftOpt : binExpr.getLeft(),
                        binExpr.getOperator(),
                        rightOpt != null ? rightOpt : binExpr.getRight());
            }
        }

        // Recursively optimize unary expressions
        if (expr instanceof UnaryExpression) {
            UnaryExpression unaryExpr = (UnaryExpression) expr;
            Expression operandOpt = optimizeExpression(unaryExpr.getExpression());
            if (operandOpt != null) {
                return new UnaryExpression(unaryExpr.getOperator(), operandOpt);
            }
        }

        return null;
    }

    /**
     * Checks if an expression consists only of constant values
     */
    private boolean isConstantExpression(Expression expr) {
        if (expr instanceof IntegerLiteral ||
                expr instanceof RealLiteral ||
                expr instanceof BooleanLiteral ||
                expr instanceof StringLiteral) {
            return true;
        }

        if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            return isConstantExpression(binExpr.getLeft()) &&
                    isConstantExpression(binExpr.getRight());
        }

        if (expr instanceof UnaryExpression) {
            UnaryExpression unaryExpr = (UnaryExpression) expr;
            return isConstantExpression(unaryExpr.getExpression());
        }

        return false;
    }

    /**
     * Evaluates a constant expression to its value
     */
    private Object evaluateConstant(Expression expr) {
        if (expr instanceof IntegerLiteral) {
            return ((IntegerLiteral) expr).evaluate();
        } else if (expr instanceof RealLiteral) {
            return ((RealLiteral) expr).evaluate();
        } else if (expr instanceof BooleanLiteral) {
            return ((BooleanLiteral) expr).evaluate();
        } else if (expr instanceof StringLiteral) {
            return ((StringLiteral) expr).evaluate();
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression binExpr = (BinaryExpression) expr;
            Object left = evaluateConstant(binExpr.getLeft());
            Object right = evaluateConstant(binExpr.getRight());
            return evaluateBinaryOperation(left, right, binExpr.getOperator());
        } else if (expr instanceof UnaryExpression) {
            UnaryExpression unaryExpr = (UnaryExpression) expr;
            Object operand = evaluateConstant(unaryExpr.getExpression());
            return evaluateUnaryOperation(operand, unaryExpr.getOperator());
        }
        throw new RuntimeException("Cannot evaluate non-constant expression");
    }

    /**
     * Evaluates a binary operation on constant values
     */
    private Object evaluateBinaryOperation(Object left, Object right, String operator) {
        if (left instanceof Integer && right instanceof Integer) {
            int l = (Integer) left;
            int r = (Integer) right;
            switch (operator) {
                case "+":
                    return l + r;
                case "-":
                    return l - r;
                case "*":
                    return l * r;
                case "/":
                    return l / r;
                case "%":
                    return l % r;
                case "<":
                    return l < r;
                case "<=":
                    return l <= r;
                case ">":
                    return l > r;
                case ">=":
                    return l >= r;
                case "=":
                    return l == r;
                case "!=":
                    return l != r;
            }
        } else if (left instanceof Boolean && right instanceof Boolean) {
            boolean l = (Boolean) left;
            boolean r = (Boolean) right;
            switch (operator) {
                case "and":
                    return l && r;
                case "or":
                    return l || r;
                case "xor":
                    return l ^ r;
            }
        }
        throw new RuntimeException("Invalid operands for operator " + operator);
    }

    /**
     * Evaluates a unary operation on a constant value
     */
    private Object evaluateUnaryOperation(Object operand, String operator) {
        if (operand instanceof Integer && operator.equals("-")) {
            return -(Integer) operand;
        } else if (operand instanceof Boolean && operator.equals("not")) {
            return !(Boolean) operand;
        }
        throw new RuntimeException("Invalid operand for operator " + operator);
    }

    private boolean isValidCast(Type sourceType, Type targetType) {
        if (sourceType == null || targetType == null) {
            return false;
        }

        if (sourceType instanceof SimpleType && targetType instanceof SimpleType) {
            String sourceName = ((SimpleType) sourceType).getName();
            String targetName = ((SimpleType) targetType).getName();

            // Same type casts are always valid
            if (sourceName.equals(targetName)) {
                return true;
            }

            // Numeric conversions (both ways)
            if ((sourceName.equals("integer") || sourceName.equals("real")) &&
                    (targetName.equals("integer") || targetName.equals("real"))) {
                return true;
            }

            // Boolean conversions
            if (sourceName.equals("boolean") && targetName.equals("integer")) {
                return true; // boolean to integer
            }
            if (sourceName.equals("integer") && targetName.equals("boolean")) {
                return true; // integer to boolean
            }
            if (sourceName.equals("real") && targetName.equals("boolean")) {
                return true; // real to boolean (via integer)
            }
            if (sourceName.equals("boolean") && targetName.equals("real")) {
                return true; // boolean to real (via integer)
            }
        }

        return false;
    }
}