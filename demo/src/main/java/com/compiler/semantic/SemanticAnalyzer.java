package com.compiler.semantic;

import com.compiler.*;
import java.util.*;

public class SemanticAnalyzer {
    private SymbolTable symbolTable;
    private List<SemanticError> errors;
    private Stack<Type> expectedReturnTypes;
    private Set<String> usedVariables;

    public SemanticAnalyzer() {
        this.symbolTable = new SymbolTable();
        this.errors = new ArrayList<>();
        this.expectedReturnTypes = new Stack<>();
        this.usedVariables = new HashSet<>();
    }

    public List<SemanticError> analyze(Program program) {
        errors.clear();
        symbolTable.clear();
        usedVariables.clear();
        visit(program);
        removeUnusedVariables(program);
        return errors;
    }

    private void visit(Program program) {
        // First pass: collect all global declarations
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl) {
                RoutineDecl routine = (RoutineDecl) stmt;
                symbolTable.declareRoutine(routine.getName(), routine);
            } else if (stmt instanceof TypeDecl) {
                TypeDecl type = (TypeDecl) stmt;
                symbolTable.declareType(type.getName(), type.getType());
            } else if (stmt instanceof VarDecl) {
                VarDecl varDecl = (VarDecl) stmt;
                symbolTable.declareVariable(varDecl.getName(), varDecl.getType());
            }
        }

        // Second pass: analyze all statements
        for (Statement stmt : program.getStatements()) {
            visitStatement(stmt);
        }
    }

    private void visitStatement(Statement stmt) {
        if (stmt instanceof VarDecl) {
            visitVarDecl((VarDecl) stmt);
        } else if (stmt instanceof Assignment) {
            visitAssignment((Assignment) stmt);
        } else if (stmt instanceof IfStatement) {
            visitIfStatement((IfStatement) stmt);
        } else if (stmt instanceof WhileStatement) {
            visitWhileStatement((WhileStatement) stmt);
        } else if (stmt instanceof ForLoop) {
            visitForLoop((ForLoop) stmt);
        } else if (stmt instanceof ReturnStatement) {
            visitReturnStatement((ReturnStatement) stmt);
        } else if (stmt instanceof PrintStatement) {
            visitPrintStatement((PrintStatement) stmt);
        } else if (stmt instanceof ReadStatement) {
            visitReadStatement((ReadStatement) stmt);
        } else if (stmt instanceof RoutineDecl) {
            visitRoutineDecl((RoutineDecl) stmt);
        }
    }

    private void visitVarDecl(VarDecl decl) {
        // Check if variable is already declared in current scope
        if (symbolTable.isDefinedInCurrentScope(decl.getName())) {
            errors.add(new SemanticError("Variable " + decl.getName() + " is already declared in this scope"));
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
            if (!isTypeCompatible(declaredType, initType)) {
                errors.add(new SemanticError("Cannot initialize variable of type " +
                        declaredType + " with value of type " + initType));
                return;
            }
        }

        symbolTable.declareVariable(decl.getName(), declaredType);
    }

    private void visitAssignment(Assignment assign) {
        // Check if variable exists
        if (!symbolTable.isDefined(assign.getTarget())) {
            errors.add(new SemanticError("Undefined variable " + assign.getTarget()));
            return;
        }

        Type targetType = symbolTable.getType(assign.getTarget());
        Type valueType = getExpressionType(assign.getValue());

        if (!isTypeCompatible(targetType, valueType)) {
            errors.add(new SemanticError("Cannot assign value of type " +
                    valueType + " to variable of type " + targetType));
        }

        // Mark variable as used
        usedVariables.add(assign.getTarget());
    }

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

    private void visitReturnStatement(ReturnStatement returnStmt) {
        // Check if the return type is compatible with the expected return type
        Type returnType = getExpressionType(returnStmt.getExpression());
        if (!isTypeCompatible(expectedReturnTypes.peek(), returnType)) {
            errors.add(new SemanticError("Return statement type mismatch"));
        }
    }

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

    private void visitReadStatement(ReadStatement readStmt) {
        // Check if the variable exists
        if (!symbolTable.isDefined(readStmt.getVariable())) {
            errors.add(new SemanticError("Undefined variable " + readStmt.getVariable()));
        }

        // Mark variable as used
        usedVariables.add(readStmt.getVariable());
    }

    private void visitRoutineDecl(RoutineDecl routine) {
        // Create a new scope for the routine
        symbolTable.enterScope();

        // Add parameters to the new scope
        if (routine.getParameters() != null) {
            for (Parameter param : routine.getParameters()) {
                symbolTable.declareVariable(param.getName(), param.getType());
            }
        }

        // Push return type for checking return statements
        if (routine.getReturnType() != null) {
            expectedReturnTypes.push(routine.getReturnType());
        }

        // Visit the routine body
        for (Statement stmt : routine.getBody()) {
            visitStatement(stmt);
        }

        // Pop return type if we pushed one
        if (routine.getReturnType() != null) {
            expectedReturnTypes.pop();
        }

        // Exit the routine's scope
        symbolTable.exitScope();
    }

    private Type getExpressionType(Expression expr) {
        if (expr == null) {
            return null;
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
            if (!symbolTable.isDefined(varName)) {
                errors.add(new SemanticError("Undefined variable '" + varName + "'"));
                return null;
            }
            return symbolTable.getType(varName);
        } else if (expr instanceof BinaryExpression) {
            return getExpressionType(((BinaryExpression) expr).getLeft());
        } else if (expr instanceof UnaryExpression) {
            return getExpressionType(((UnaryExpression) expr).getExpression());
        }
        return null;
    }

    private boolean isValidType(Type type) {
        // Check if the type is valid (exists in the symbol table)
        return symbolTable.isTypeDefined(type);
    }

    private boolean isTypeCompatible(Type expected, Type actual) {
        // Implement type compatibility logic
        return expected.equals(actual);
    }

    private boolean isBoolean(Type type) {
        return type.equals(new SimpleType("boolean"));
    }

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
}