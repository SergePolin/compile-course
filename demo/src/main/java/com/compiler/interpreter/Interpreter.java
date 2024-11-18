package com.compiler.interpreter;

import com.compiler.*;
import java.util.*;
import java.io.*;

public class Interpreter {
    private Map<String, Object> globalVariables;
    private Stack<Map<String, Object>> scopeStack;
    private boolean returnFlag;
    private Object returnValue;
    private boolean debug = true;
    private int indentLevel = 0;
    private Map<String, RoutineDecl> routines = new HashMap<>();

    public Interpreter() {
        this.globalVariables = new HashMap<>();
        this.scopeStack = new Stack<>();
        this.returnFlag = false;
        this.returnValue = null;
        scopeStack.push(new HashMap<>()); // Push global scope
    }

    private void debug(String message) {
        if (debug) {
            System.out.println("DEBUG: " + "  ".repeat(indentLevel) + message);
        }
    }

    public void interpret(Program program) {
        try {
            debug("Starting program interpretation");
            indentLevel++;
            
            // First pass: register all routines
            debug("Registering routines");
            for (Statement stmt : program.getStatements()) {
                if (stmt instanceof RoutineDecl) {
                    executeRoutineDecl((RoutineDecl) stmt);
                }
            }
            
            // Second pass: execute main routine
            debug("Looking for main routine");
            RoutineDecl mainRoutine = routines.get("main");
            if (mainRoutine == null) {
                throw new InterpreterException("No main routine found");
            }
            
            debug("Executing main routine");
            // Execute all statements in main routine
            scopeStack.push(new HashMap<>()); // Create scope for main
            try {
                for (Statement stmt : mainRoutine.getBody()) {
                    executeStatement(stmt);
                }
            } finally {
                scopeStack.pop();
            }
            
            indentLevel--;
            debug("Program execution completed");
        } catch (InterpreterException e) {
            System.err.println("Runtime error: " + e.getMessage());
            System.exit(1);
        }
    }

    private void executeStatement(Statement stmt) {
        indentLevel++;
        if (stmt instanceof VarDecl) {
            debug("Executing variable declaration");
            executeVarDecl((VarDecl) stmt);
        } else if (stmt instanceof Assignment) {
            debug("Executing assignment");
            executeAssignment((Assignment) stmt);
        } else if (stmt instanceof IfStatement) {
            debug("Executing if statement");
            executeIfStatement((IfStatement) stmt);
        } else if (stmt instanceof WhileStatement) {
            debug("Executing while statement");
            executeWhileStatement((WhileStatement) stmt);
        } else if (stmt instanceof PrintStatement) {
            debug("Executing print statement");
            executePrintStatement((PrintStatement) stmt);
        } else if (stmt instanceof ReadStatement) {
            debug("Executing read statement");
            executeReadStatement((ReadStatement) stmt);
        } else if (stmt instanceof RoutineDecl) {
            debug("Executing routine declaration");
            executeRoutineDecl((RoutineDecl) stmt);
        } else if (stmt instanceof ForLoop) {
            debug("Executing for loop");
            executeForLoop((ForLoop) stmt);
        } else if (stmt instanceof ReturnStatement) {
            debug("Executing return statement");
            executeReturnStatement((ReturnStatement) stmt);
        } else {
            debug("Unknown statement type: " + stmt.getClass().getName());
        }
        indentLevel--;
    }

    private void executeVarDecl(VarDecl decl) {
        Object value = null;
        if (decl.getInitializer() != null) {
            debug("Evaluating initializer expression");
            value = evaluateExpression(decl.getInitializer());
        }
        debug("Declaring variable '" + decl.getName() + "' with initial value: " + value);
        getCurrentScope().put(decl.getName(), value);
    }

    private void executeAssignment(Assignment assign) {
        debug("Evaluating right-hand side expression");
        Object value = evaluateExpression(assign.getValue());
        String varName = assign.getTarget();
        
        debug("Looking up variable '" + varName + "' in scope");
        Map<String, Object> scope = findScopeWithVariable(varName);
        if (scope == null) {
            throw new InterpreterException("Undefined variable: " + varName);
        }
        debug("Assigning value " + value + " to variable '" + varName + "'");
        scope.put(varName, value);
    }

    private void executeIfStatement(IfStatement ifStmt) {
        debug("Evaluating if condition");
        Object condition = evaluateExpression(ifStmt.getCondition());
        if (!(condition instanceof Boolean)) {
            throw new InterpreterException("If condition must be boolean");
        }

        debug("Condition evaluated to: " + condition);
        if ((Boolean) condition) {
            debug("Executing 'then' branch");
            executeStatements(ifStmt.getThenStatements());
        } else if (ifStmt.getElseStatements() != null) {
            debug("Executing 'else' branch");
            executeStatements(ifStmt.getElseStatements());
        }
    }

    private void executeWhileStatement(WhileStatement whileStmt) {
        debug("Starting while loop");
        int iteration = 0;
        while (true) {
            debug("While iteration " + (++iteration));
            debug("Evaluating while condition");
            Object condition = evaluateExpression(whileStmt.getCondition());
            if (!(condition instanceof Boolean)) {
                throw new InterpreterException("While condition must be boolean");
            }
            
            debug("Condition evaluated to: " + condition);
            if (!(Boolean) condition) {
                debug("While loop condition false, breaking");
                break;
            }
            
            debug("Executing while loop body");
            executeStatements(whileStmt.getBody());
            
            if (returnFlag) {
                debug("Return statement encountered, breaking while loop");
                break;
            }
        }
    }

    private void executePrintStatement(PrintStatement printStmt) {
        debug("Evaluating print expression");
        Object value = evaluateExpression(printStmt.getExpression());
        debug("Printing value: " + value);
        System.out.println(value);
    }

    private void executeReadStatement(ReadStatement readStmt) {
        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
            String input = reader.readLine();
            String varName = readStmt.getVariable();
            
            Map<String, Object> scope = findScopeWithVariable(varName);
            if (scope == null) {
                throw new InterpreterException("Undefined variable: " + varName);
            }
            
            // For now, treat all input as strings
            scope.put(varName, input);
        } catch (IOException e) {
            throw new InterpreterException("Error reading input: " + e.getMessage());
        }
    }

    private void executeRoutineDecl(RoutineDecl routine) {
        debug("Registering routine: " + routine.getName());
        routines.put(routine.getName(), routine);
    }

    private Object executeRoutineCall(RoutineCall call) {
        debug("Executing routine call: " + call.getName());
        RoutineDecl routine = routines.get(call.getName());
        if (routine == null) {
            throw new InterpreterException("Undefined routine: " + call.getName());
        }

        // Create new scope for routine parameters and local variables
        scopeStack.push(new HashMap<>());
        try {
            // Evaluate and bind arguments to parameters
            List<Parameter> params = routine.getParameters();
            List<Expression> args = call.getArguments();
            
            if (params.size() != args.size()) {
                throw new InterpreterException("Wrong number of arguments for routine " + call.getName());
            }

            for (int i = 0; i < params.size(); i++) {
                Object argValue = evaluateExpression(args.get(i));
                debug("Binding parameter '" + params.get(i).getName() + "' to value: " + argValue);
                getCurrentScope().put(params.get(i).getName(), argValue);
            }

            // Execute routine body
            returnFlag = false;
            returnValue = null;
            
            for (Statement stmt : routine.getBody()) {
                executeStatement(stmt);
                if (returnFlag) {
                    debug("Routine call result: " + returnValue);
                    return returnValue;
                }
            }

            return returnValue;
        } finally {
            scopeStack.pop();
        }
    }

    private void executeReturnStatement(ReturnStatement returnStmt) {
        debug("Executing return statement");
        if (returnStmt.getExpression() != null) {
            returnValue = evaluateExpression(returnStmt.getExpression());
        }
        returnFlag = true;
    }

    private void executeForLoop(ForLoop forLoop) {
        debug("Starting for loop execution");
        String loopVar = forLoop.getVariable();
        boolean isReverse = forLoop.isReverse();
        
        // Evaluate start and end expressions
        Object startVal = evaluateExpression(forLoop.getRangeStart());
        Object endVal = evaluateExpression(forLoop.getRangeEnd());
        
        if (!(startVal instanceof Integer) || !(endVal instanceof Integer)) {
            throw new InterpreterException("For loop range must be integer values");
        }
        
        int start = (Integer) startVal;
        int end = (Integer) endVal;
        
        // Create a new scope for the loop
        scopeStack.push(new HashMap<>());
        try {
            // Initialize loop variable
            getCurrentScope().put(loopVar, start);
            
            // Execute loop
            if (!isReverse) {
                for (int i = start; i <= end; i++) {
                    debug("For loop iteration: " + i);
                    getCurrentScope().put(loopVar, i);
                    executeStatements(forLoop.getBody());
                    
                    if (returnFlag) {
                        debug("Return statement encountered, breaking for loop");
                        break;
                    }
                }
            } else {
                for (int i = start; i >= end; i--) {
                    debug("For loop iteration: " + i);
                    getCurrentScope().put(loopVar, i);
                    executeStatements(forLoop.getBody());
                    
                    if (returnFlag) {
                        debug("Return statement encountered, breaking for loop");
                        break;
                    }
                }
            }
        } finally {
            scopeStack.pop();
        }
        debug("For loop completed");
    }

    private Object evaluateExpression(Expression expr) {
        indentLevel++;
        debug("Evaluating expression of type: " + expr.getClass().getSimpleName());
        Object result = null;
        
        if (expr instanceof IntegerLiteral) {
            result = ((IntegerLiteral) expr).evaluate();
            debug("Integer literal value: " + result);
        } else if (expr instanceof RealLiteral) {
            result = ((RealLiteral) expr).evaluate();
            debug("Real literal value: " + result);
        } else if (expr instanceof StringLiteral) {
            result = ((StringLiteral) expr).evaluate();
            debug("String literal value: " + result);
        } else if (expr instanceof BooleanLiteral) {
            result = ((BooleanLiteral) expr).evaluate();
            debug("Boolean literal value: " + result);
        } else if (expr instanceof VariableReference) {
            String name = ((VariableReference) expr).getName();
            debug("Looking up variable: " + name);
            result = evaluateVariable(name);
            debug("Variable value: " + result);
        } else if (expr instanceof BinaryExpression) {
            debug("Evaluating binary expression");
            result = evaluateBinaryExpression((BinaryExpression) expr);
            debug("Binary expression result: " + result);
        } else if (expr instanceof UnaryExpression) {
            debug("Evaluating unary expression");
            result = evaluateUnaryExpression((UnaryExpression) expr);
            debug("Unary expression result: " + result);
        } else if (expr instanceof RoutineCall) {
            debug("Evaluating routine call");
            result = executeRoutineCall((RoutineCall) expr);
            debug("Routine call result: " + result);
        }
        
        indentLevel--;
        return result;
    }

    private Object evaluateBinaryExpression(BinaryExpression expr) {
        debug("Evaluating binary expression");
        Object left = evaluateExpression(expr.getLeft());
        Object right = evaluateExpression(expr.getRight());
        String op = expr.getOperator();

        switch (op) {
            case "+":
                // Handle string concatenation
                if (left instanceof String || right instanceof String) {
                    return String.valueOf(left) + String.valueOf(right);
                }
                // Handle numeric addition
                if (left instanceof Integer && right instanceof Integer) {
                    return (Integer)left + (Integer)right;
                }
                return toDouble(left) + toDouble(right);
            case "-":
                if (left instanceof Integer && right instanceof Integer) {
                    return (Integer)left - (Integer)right;
                }
                return toDouble(left) - toDouble(right);
            case "*":
                if (left instanceof Integer && right instanceof Integer) {
                    return (Integer)left * (Integer)right;
                }
                return toDouble(left) * toDouble(right);
            case "/":
                if (toDouble(right) == 0) {
                    throw new InterpreterException("Division by zero");
                }
                if (left instanceof Integer && right instanceof Integer) {
                    return (Integer)left / (Integer)right;
                }
                return toDouble(left) / toDouble(right);
            case "and":
                return toBoolean(left) && toBoolean(right);
            case "or":
                return toBoolean(left) || toBoolean(right);
            case "xor":
                return toBoolean(left) ^ toBoolean(right);
            case "=":
                return left.equals(right);
            case "!=":
                return !left.equals(right);
            case "<":
                return compareValues(left, right) < 0;
            case "<=":
                return compareValues(left, right) <= 0;
            case ">":
                return compareValues(left, right) > 0;
            case ">=":
                return compareValues(left, right) >= 0;
        }
        throw new InterpreterException("Unsupported operator: " + op);
    }

    private Object evaluateUnaryExpression(UnaryExpression expr) {
        Object operand = evaluateExpression(expr.getExpression());
        String op = expr.getOperator();

        switch (op) {
            case "-":
                if (operand instanceof Integer) {
                    return -(Integer) operand;
                }
                if (operand instanceof Double) {
                    return -(Double) operand;
                }
                break;
            case "not":
                return !toBoolean(operand);
        }
        throw new InterpreterException("Unsupported unary operator: " + op);
    }

    private Object evaluateVariable(String name) {
        Map<String, Object> scope = findScopeWithVariable(name);
        if (scope == null) {
            throw new InterpreterException("Undefined variable: " + name);
        }
        return scope.get(name);
    }

    private void executeStatements(List<Statement> statements) {
        // Only create new scope for routine bodies and control structures
        for (Statement stmt : statements) {
            executeStatement(stmt);
            if (returnFlag) {
                break;
            }
        }
    }

    private Map<String, Object> getCurrentScope() {
        return scopeStack.peek();
    }

    private Map<String, Object> findScopeWithVariable(String name) {
        for (int i = scopeStack.size() - 1; i >= 0; i--) {
            Map<String, Object> scope = scopeStack.get(i);
            if (scope.containsKey(name)) {
                return scope;
            }
        }
        return null;
    }

    private double toDouble(Object value) {
        if (value instanceof Integer) {
            return ((Integer) value).doubleValue();
        }
        if (value instanceof Double) {
            return (Double) value;
        }
        throw new InterpreterException("Cannot convert to number: " + value);
    }

    private boolean toBoolean(Object value) {
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        throw new InterpreterException("Cannot convert to boolean: " + value);
    }

    private int compareValues(Object left, Object right) {
        if (left instanceof Number && right instanceof Number) {
            return Double.compare(toDouble(left), toDouble(right));
        }
        if (left instanceof String && right instanceof String) {
            return ((String) left).compareTo((String) right);
        }
        throw new InterpreterException("Cannot compare values: " + left + " and " + right);
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public boolean isDebug() {
        return debug;
    }
} 