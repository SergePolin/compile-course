package com.compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.compiler.semantic.SymbolTable;
import java.io.FileWriter;
import java.io.IOException;
import java.io.File;

public class JasminCodeGenerator {
    private int labelCounter = 0;
    private Map<String, Integer> localVariables = new HashMap<>();
    private Map<String, Type> variableTypes = new HashMap<>();
    private Map<String, Type> globalVariableTypes = new HashMap<>();

    private int nextIntVariable = 1;
    private int nextLocalVariableIndex = 1;
    private int nextDoubleVariable = 10;  // Start doubles at a higher index to avoid overlap
    private SymbolTable symbolTable;

    public JasminCodeGenerator(SymbolTable symbolTable) {
        this.symbolTable = symbolTable;
    }

    public String generate(Program program) {
    System.out.println("[DEBUG] Starting code generation");
    localVariables.clear();
    variableTypes.clear();
    StringBuilder sb = new StringBuilder();

    // Generate record type classes first
    for (Statement stmt : program.getStatements()) {
        if (stmt instanceof RecordTypeDecl) {
            generateRecordTypeClass((RecordTypeDecl) stmt);
        }
    }

    // Class header
    sb.append(".class public Main\n");
    sb.append(".super java/lang/Object\n\n");

    // Generate global variable fields
    for (Statement stmt : program.getStatements()) {
        if (stmt instanceof VarDecl || stmt instanceof ArrayDecl) {
            generateGlobalVariableField(stmt, sb);
        }
    }

    // Default constructor with proper limits
    sb.append(".method public <init>()V\n");
    sb.append("    aload_0\n");
    sb.append("    invokespecial java/lang/Object/<init>()V\n");
    sb.append("    return\n");
    sb.append(".end method\n\n");

    // Remove generateStaticInitializer call

    // Generate all routines
    for (Statement stmt : program.getStatements()) {
        if (stmt instanceof RoutineDecl) {
            generateRoutineDecl(program, (RoutineDecl) stmt, sb); // Pass 'program' here
        }
    }

    String result = sb.toString();
    System.out.println("[DEBUG] Generated Jasmin code:\n" + result);
    return result;
}




private void generateGlobalVariableField(Statement stmt, StringBuilder sb) {
    if (stmt instanceof VarDecl) {
        VarDecl varDecl = (VarDecl) stmt;
        String fieldDescriptor = getTypeDescriptor(varDecl.getType());
        sb.append(".field private static ")
          .append(varDecl.getName())
          .append(" ")
          .append(fieldDescriptor)
          .append("\n\n");

        // Store the type in globalVariableTypes
        globalVariableTypes.put(varDecl.getName(), varDecl.getType());

    } else if (stmt instanceof ArrayDecl) {
        ArrayDecl arrayDecl = (ArrayDecl) stmt;
        Type elementType = ((ArrayType) arrayDecl.getType()).getElementType();
        String elementTypeDescriptor = getArrayFieldDescriptor(elementType);
        String fieldDescriptor = "[" + elementTypeDescriptor;

        sb.append(".field private static ")
          .append(arrayDecl.getName())
          .append(" ")
          .append(fieldDescriptor)
          .append("\n\n");

        // Store the type in globalVariableTypes
        globalVariableTypes.put(arrayDecl.getName(), arrayDecl.getType());
    }
}



private void generateArrayInitialization(ArrayDecl arrayDecl, StringBuilder sb) {
    Type elementType = ((ArrayType) arrayDecl.getType()).getElementType();
    int size = ((ArrayType) arrayDecl.getType()).getSize();

    // Initialize array
    sb.append("    iconst_").append(size).append("\n");
    sb.append("    newarray ").append(getArrayTypeDescriptor(elementType)).append("\n");
    sb.append("    putstatic Main/").append(arrayDecl.getName())
      .append(" ").append("[").append(getArrayFieldDescriptor(elementType)).append("\n");
}


    private void generateRoutineDecl(Program program, RoutineDecl routine, StringBuilder sb) {
    // Clear local variables for new routine
    localVariables.clear();
    variableTypes.clear();
    nextLocalVariableIndex = 1; // Start at 1 for methods (args array at index 0)

    String methodName = routine.getName();
    Type returnType = routine.getReturnType();
    List<Parameter> params = routine.getParameters();

    // Generate method signature
    if (methodName.equals("main")) {
        sb.append(".method public static main([Ljava/lang/String;)V\n");

        // Stack and locals limits adjusted to match the working code
        sb.append("    .limit stack 4\n");
        sb.append("    .limit locals 2\n");

        // Initialize arrays in main method
         for (Statement stmt : program.getStatements()) {
            if (stmt instanceof ArrayDecl) {
                generateArrayInitialization((ArrayDecl) stmt, sb);
            }
        }

        // Initialize record instances
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VarDecl) {
                VarDecl varDecl = (VarDecl) stmt;
                if (varDecl.getType() instanceof SimpleType) {
                    String typeName = ((SimpleType) varDecl.getType()).getName();
                    if (symbolTable.getTypeDefinition(typeName) instanceof RecordType) {
                        // Initialize record instance
                        sb.append("    new ").append(typeName).append("\n");
                        sb.append("    dup\n");
                        sb.append("    invokespecial ").append(typeName).append("/<init>()V\n");
                        sb.append("    putstatic Main/").append(varDecl.getName())
                          .append(" L").append(typeName).append(";\n");
                    }
                }
            }
        }
    } else {
        // Existing code for other routines
    }

    // Generate routine body
    for (Statement stmt : routine.getBody()) {
        generateStatement(program, stmt, sb);
    }

    // Add return statement if needed
    if (returnType == null || methodName.equals("main")) {
        sb.append("    return\n");
    }

    sb.append(".end method\n\n");
}



private int getTypeSlotSize(Type type) {
    if (type instanceof SimpleType && ((SimpleType) type).getName().equals("real")) {
        return 2; // Doubles take two slots
    } else {
        return 1; // Most types take one slot
    }
}



    private String getTypeDescriptor(Type type) {
    if (type == null || type == Type.VOID) {
        return "V";
    } else if (type == Type.INTEGER) {
        return "I";
    } else if (type == Type.BOOLEAN) {
        return "Z";
    } else if (type == Type.STRING) {
        return "Ljava/lang/String;";
    } else if (type instanceof SimpleType) {
        String typeName = ((SimpleType) type).getName();
        if (typeName.equals("real")) {
            return "D";
        }
        // For record types, return the class descriptor
        return "L" + typeName + ";";
    }
    throw new RuntimeException("Unsupported type: " + type);
}


    private void generateRoutineCall(RoutineCall call, StringBuilder sb) {
    System.out.println("[DEBUG] Generating routine call: " + call.getName());

    // Retrieve the RoutineDecl from the symbol table
    RoutineDecl routine = symbolTable.getRoutine(call.getName());
    List<Parameter> params = routine.getParameters();

    // Generate code for arguments and handle implicit casting
    List<Expression> arguments = call.getArguments();
    for (int i = 0; i < arguments.size(); i++) {
        Expression arg = arguments.get(i);
        generateExpression(arg, sb);

        // Get the parameter type from the RoutineDecl
        Type paramType = params.get(i).getType();
        Type argType = getExpressionType(arg);
        generateImplicitCast(argType, paramType, sb);
    }

    // Generate invocation
    sb.append("    invokestatic Main/").append(call.getName()).append("(");

    // Add parameter descriptors
    for (Parameter param : routine.getParameters()) {
        sb.append(getTypeDescriptor(param.getType()));
    }

    // Add return type
    sb.append(")").append(getTypeDescriptor(routine.getReturnType())).append("\n");
}


    private void generateStatement(Program program, Statement stmt, StringBuilder sb) {
        if (stmt instanceof VarDecl) {
            generateVarDecl((VarDecl) stmt, sb);
        } else if (stmt instanceof ArrayDecl) {
            generateArrayDecl((ArrayDecl) stmt, sb);
        } else if (stmt instanceof PrintStatement) {
            generatePrintStatement((PrintStatement) stmt, sb);
        } else if (stmt instanceof RoutineDecl) {
            generateRoutineDecl(program, (RoutineDecl) stmt, sb);
        } else if (stmt instanceof IfStatement) {
            generateIfStatement(program, (IfStatement) stmt, sb);
        } else if (stmt instanceof WhileStatement) {
            generateWhileStatement(program, (WhileStatement) stmt, sb);
        } else if (stmt instanceof Assignment) {
            generateAssignment((Assignment) stmt, sb);
        } else if (stmt instanceof ForLoop) {
            generateForLoop(program, (ForLoop) stmt, sb);
        } else if (stmt instanceof ReturnStatement) {
            generateReturnStatement((ReturnStatement) stmt, sb);
        }
    }


    private void generateVarDecl(VarDecl decl, StringBuilder sb) {
    System.out.println("[DEBUG] Generating variable declaration: " + decl.getName());
    Type type = decl.getType();
    int varIndex = nextIntVariable++;
    
    localVariables.put(decl.getName(), varIndex);
    variableTypes.put(decl.getName(), type);

    if (decl.getInitializer() != null) {
        // Add comment for clarity
        sb.append("    ; var ").append(decl.getName()).append(": ").append(type).append("\n");

        if (decl.getInitializer() instanceof TypeCast) {
            TypeCast cast = (TypeCast) decl.getInitializer();
            generateExpression(cast.getExpression(), sb);
            generateTypeCastAndStore(getExpressionType(cast.getExpression()), type, varIndex, sb);
        } else {
            generateExpression(decl.getInitializer(), sb);
            generateStore(type, varIndex, sb);
        }
        sb.append("\n");
    }
}


    private void generateStore(Type type, int varIndex, StringBuilder sb) {
        if (type == Type.INTEGER || type == Type.BOOLEAN) {
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (type instanceof SimpleType && ((SimpleType)type).getName().equals("real")) {
            sb.append("    fstore ").append(varIndex).append("\n");  // Use fstore instead of dstore
        }
    }

    private void generateLoad(Type type, int varIndex, StringBuilder sb) {
        if (type == Type.INTEGER || type == Type.BOOLEAN) {
            sb.append("    iload ").append(varIndex).append("\n");
        } else if (type instanceof SimpleType && ((SimpleType)type).getName().equals("real")) {
            sb.append("    fload ").append(varIndex).append("\n");  // Use fload instead of dload
        }
    }

    private void generatePrintStatement(PrintStatement stmt, StringBuilder sb) {
    sb.append("    getstatic java/lang/System/out Ljava/io/PrintStream;\n");
    
    Expression expr = stmt.getExpression();
    Type exprType = getExpressionType(expr);
    generateExpression(expr, sb);
    
    if (exprType == Type.INTEGER) {
        sb.append("    invokevirtual java/io/PrintStream/println(I)V\n");
    } else if (exprType instanceof SimpleType &&
            ((SimpleType) exprType).getName().equals("real")) {
        sb.append("    invokevirtual java/io/PrintStream/println(D)V\n");  // Use D for double
    } else if (exprType == Type.BOOLEAN) {
        sb.append("    invokevirtual java/io/PrintStream/println(Z)V\n");
    } else if (exprType == Type.STRING) {
        sb.append("    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
    } else {
        throw new RuntimeException("Unsupported print expression type: " + exprType);
    }
    sb.append("\n");
}




    private void generateStringConcatenation(BinaryExpression expr, StringBuilder sb) {
        if (expr.getLeft() instanceof BinaryExpression && 
            ((BinaryExpression)expr.getLeft()).getOperator().equals("+")) {
            generateStringConcatenation((BinaryExpression)expr.getLeft(), sb);
        } else {
            generateExpression(expr.getLeft(), sb);
            sb.append("    invokevirtual java/lang/StringBuilder/append(");
            appendAppropriateType(getExpressionType(expr.getLeft()), sb);
            sb.append(")Ljava/lang/StringBuilder;\n");
        }
        
        generateExpression(expr.getRight(), sb);
        sb.append("    invokevirtual java/lang/StringBuilder/append(");
        appendAppropriateType(getExpressionType(expr.getRight()), sb);
        sb.append(")Ljava/lang/StringBuilder;\n");
    }

    private void appendAppropriateType(Type type, StringBuilder sb) {
        if (type == Type.INTEGER) {
            sb.append("I");
        } else if (type instanceof SimpleType && 
                  ((SimpleType)type).getName().equals("real")) {
            sb.append("D");
        } else if (type == Type.BOOLEAN) {
            sb.append("Z");
        } else {
            sb.append("Ljava/lang/String;");
        }
    }

    private Type getExpressionType(Expression expr) {
        if (expr instanceof IntegerLiteral) return Type.INTEGER;
        if (expr instanceof RealLiteral) return new SimpleType("real");
        if (expr instanceof BooleanLiteral) return Type.BOOLEAN;
        if (expr instanceof StringLiteral) return Type.STRING;
        if (expr instanceof VariableReference) {
            return variableTypes.get(((VariableReference) expr).getName());
        }
        if (expr instanceof TypeCast) {
            return ((TypeCast) expr).getTargetType();
        }
        return Type.INTEGER;
    }

    private void generateExpression(Expression expr, StringBuilder sb) {
        System.out.println("[DEBUG] Generating expression: " + expr.getClass().getSimpleName());
        if (expr instanceof IntegerLiteral) {
            int value = ((IntegerLiteral) expr).getValue();
            if (value >= -1 && value <= 5) {
                sb.append("    iconst_").append(value).append("\n");
            } else if (value >= -128 && value <= 127) {
                sb.append("    bipush ").append(value).append("\n");
            } 
            else if (value >= -32768 && value <= 32767) {
                sb.append("    sipush ").append(value).append("\n");
            }
            else {
                sb.append("    ldc ").append(value).append("\n");
            }
        } else if (expr instanceof RealLiteral) {
            double value = ((RealLiteral) expr).getValue();
            sb.append("    ldc ").append(value).append("\n");  // Use ldc instead of ldc2_w
        } else if (expr instanceof BooleanLiteral) {
            sb.append("    iconst_").append(((BooleanLiteral) expr).getValue() ? "1" : "0").append("\n");
        } else if (expr instanceof StringLiteral) {
            sb.append("    ldc \"").append(((StringLiteral) expr).getValue()).append("\"\n");
        } else if (expr instanceof VariableReference) {
            String varName = ((VariableReference) expr).getName();
            Integer varIndex = localVariables.get(varName);
            Type varType = variableTypes.get(varName);
            System.out.println("[DEBUG] Loading variable " + varName + " of type " + varType + " from index " + varIndex);
            generateLoad(varType, varIndex, sb);
        } else if (expr instanceof TypeCast) {
            generateTypeCast((TypeCast) expr, sb);
        } else if (expr instanceof BinaryExpression) {
            generateBinaryExpression((BinaryExpression) expr, sb);
        } else if (expr instanceof RoutineCall) {
            generateRoutineCall((RoutineCall) expr, sb);
        } else if (expr instanceof ArrayAccess) {
    ArrayAccess access = (ArrayAccess) expr;
    String arrayName = access.getArray();
    Type arrayType = variableTypes.get(arrayName);

    // Load array reference
    if (arrayType == null) {
        // Global array
        Type arrayGlobalType = globalVariableTypes.get(arrayName);
        if (arrayGlobalType == null) {
            throw new RuntimeException("Undefined global variable: " + arrayName);
        }
        Type elementType = ((ArrayType) arrayGlobalType).getElementType();
        sb.append("    getstatic Main/").append(arrayName)
          .append(" [").append(getArrayFieldDescriptor(elementType)).append("\n");
    } else {
        // Local array
        Integer arrayIndex = localVariables.get(arrayName);
        sb.append("    aload ").append(arrayIndex).append("\n");
    }

    // Generate index expression
    generateExpression(access.getIndex(), sb);
    sb.append("    iconst_1\n");
    sb.append("    isub\n"); // Adjust index for zero-based arrays

    // Load array element
    Type elementType = (arrayType != null)
            ? ((ArrayType) arrayType).getElementType()
            : ((ArrayType) globalVariableTypes.get(arrayName)).getElementType();
    if (elementType == Type.INTEGER || elementType == Type.BOOLEAN) {
        sb.append("    iaload\n");
    } else if (elementType instanceof SimpleType && 
              ((SimpleType) elementType).getName().equals("real")) {
        sb.append("    daload\n");
    }
}

    }

    private void generateBinaryExpression(BinaryExpression expr, StringBuilder sb) {
        System.out.println("[DEBUG] Generating binary expression: " + expr.getOperator());
        
        if (expr.getOperator().equals("=")) {
            // Special handling for equality comparison
            generateExpression(expr.getLeft(), sb);
            generateExpression(expr.getRight(), sb);
            String label = getNextLabel();
            // For equality comparison, result is already on stack (1 for true, 0 for false)
            sb.append("    if_icmpeq ").append(label).append("_true\n");
            sb.append("    iconst_0\n");
            sb.append("    goto ").append(label).append("_end\n");
            sb.append(label).append("_true:\n");
            sb.append("    iconst_1\n");
            sb.append(label).append("_end:\n");
            return;
        }
        
        // Generate code for left and right operands
        generateExpression(expr.getLeft(), sb);
        generateExpression(expr.getRight(), sb);

        // Generate the operation
        switch (expr.getOperator()) {
            case "+": sb.append("    iadd\n"); break;
            case "-": sb.append("    isub\n"); break;
            case "*": sb.append("    imul\n"); break;
            case "/": sb.append("    idiv\n"); break;
            case "%": sb.append("    irem\n"); break;
            case "and": sb.append("    iand\n"); break;
            case "or": sb.append("    ior\n"); break;
            case "xor": sb.append("    ixor\n"); break;
            case ">": {
                String label = getNextLabel();
                sb.append("    if_icmpgt ").append(label).append("_true\n");
                sb.append("    iconst_0\n");
                sb.append("    goto ").append(label).append("_end\n");
                sb.append(label).append("_true:\n");
                sb.append("    iconst_1\n");
                sb.append(label).append("_end:\n");
                break;
            }
            case ">=": {
                String label = getNextLabel();
                sb.append("    if_icmpge ").append(label).append("_true\n");
                sb.append("    iconst_0\n");
                sb.append("    goto ").append(label).append("_end\n");
                sb.append(label).append("_true:\n");
                sb.append("    iconst_1\n");
                sb.append(label).append("_end:\n");
                break;
            }
            case "<": {
                String label = getNextLabel();
                sb.append("    if_icmplt ").append(label).append("_true\n");
                sb.append("    iconst_0\n");
                sb.append("    goto ").append(label).append("_end\n");
                sb.append(label).append("_true:\n");
                sb.append("    iconst_1\n");
                sb.append(label).append("_end:\n");
                break;
            }
            case "<=": {
                String label = getNextLabel();
                sb.append("    if_icmple ").append(label).append("_true\n");
                sb.append("    iconst_0\n");
                sb.append("    goto ").append(label).append("_end\n");
                sb.append(label).append("_true:\n");
                sb.append("    iconst_1\n");
                sb.append(label).append("_end:\n");
                break;
            }
            case "!=": {
                String label = getNextLabel();
                sb.append("    if_icmpne ").append(label).append("_true\n");
                sb.append("    iconst_0\n");
                sb.append("    goto ").append(label).append("_end\n");
                sb.append(label).append("_true:\n");
                sb.append("    iconst_1\n");
                sb.append(label).append("_end:\n");
                break;
            }
            default:
                throw new RuntimeException("Unknown operator: " + expr.getOperator());
        }
    }

    private void generateTypeCastAndStore(Type sourceType, Type targetType, int varIndex, StringBuilder sb) {
        if (sourceType == Type.INTEGER && targetType instanceof SimpleType && 
            ((SimpleType)targetType).getName().equals("real")) {
            sb.append("    i2f\n");
            sb.append("    fstore ").append(varIndex).append("\n");
        } else if (sourceType instanceof SimpleType && 
                  ((SimpleType)sourceType).getName().equals("real") && 
                  targetType == Type.INTEGER) {
            sb.append("    f2i\n");
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (sourceType == Type.BOOLEAN && targetType == Type.INTEGER) {
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (sourceType == Type.INTEGER && targetType == Type.BOOLEAN) {
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("\n");
            sb.append("    iconst_1\n");
            sb.append("    goto Store").append(label).append("\n");
            sb.append(label).append(":\n");
            sb.append("    iconst_0\n");
            sb.append("Store").append(label).append(":\n");
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (sourceType instanceof SimpleType && 
                  ((SimpleType)sourceType).getName().equals("real") && 
                  targetType == Type.BOOLEAN) {
            sb.append("    fconst_0\n");
            sb.append("    fcmpl\n");
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("\n");
            sb.append("    iconst_1\n");
            sb.append("    goto Store").append(label).append("\n");
            sb.append(label).append(":\n");
            sb.append("    iconst_0\n");
            sb.append("Store").append(label).append(":\n");
            sb.append("    istore ").append(varIndex).append("\n");
        }
    }

    private void generateTypeCast(TypeCast cast, StringBuilder sb) {
        generateExpression(cast.getExpression(), sb);
        Type sourceType = getExpressionType(cast.getExpression());
        Type targetType = cast.getTargetType();

        System.out.println("[DEBUG] Generating type cast from " + sourceType + " to " + targetType);

        if (sourceType == Type.INTEGER && targetType instanceof SimpleType && 
            ((SimpleType)targetType).getName().equals("real")) {
            sb.append("    i2f\n");
        } else if (sourceType instanceof SimpleType && 
                  ((SimpleType)sourceType).getName().equals("real") && 
                  targetType == Type.INTEGER) {
            sb.append("    f2i\n");
        } else if (sourceType == Type.BOOLEAN && targetType == Type.INTEGER) {
            // No conversion needed
        } else if (sourceType == Type.INTEGER && targetType == Type.BOOLEAN) {
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("\n");
            sb.append("    iconst_1\n");
            sb.append("    goto Store").append(label).append("\n");
            sb.append(label).append(":\n");
            sb.append("    iconst_0\n");
            sb.append("Store").append(label).append(":\n");
        } else if (sourceType instanceof SimpleType && 
                  ((SimpleType)sourceType).getName().equals("real") && 
                  targetType == Type.BOOLEAN) {
            sb.append("    fconst_0\n");
            sb.append("    fcmpl\n");
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("\n");
            sb.append("    iconst_1\n");
            sb.append("    goto Store").append(label).append("\n");
            sb.append(label).append(":\n");
            sb.append("    iconst_0\n");
            sb.append("Store").append(label).append(":\n");
        }
    }

    private void generateReturnStatement(ReturnStatement stmt, StringBuilder sb) {
    if (stmt.getExpression() != null) {
        generateExpression(stmt.getExpression(), sb);
        Type exprType = getExpressionType(stmt.getExpression());
        
        if (exprType == Type.INTEGER || exprType == Type.BOOLEAN) {
            sb.append("    ireturn\n");
        } else if (exprType instanceof SimpleType && 
                  ((SimpleType)exprType).getName().equals("real")) {
            sb.append("    dreturn\n");
        }
    } else {
        sb.append("    return\n");
    }
}


    private String getNextLabel() {
        return "L" + (labelCounter++);
    }

    private void generateIfStatement(Program program, IfStatement stmt, StringBuilder sb) {
    String elseLabel = getNextLabel();
    String endLabel = getNextLabel();
    
    // Generate condition expression
    generateExpression(stmt.getCondition(), sb);
    
    // For boolean conditions, the value is already 0 (false) or 1 (true)
    sb.append("    ifeq ").append(elseLabel).append("\n");
    
    // Generate 'then' statements
    for (Statement thenStmt : stmt.getThenStatements()) {
        generateStatement(program, thenStmt, sb);
    }
    
    // Only add 'goto endLabel' if the 'then' block doesn't end with a return
    if (!endsWithReturn(stmt.getThenStatements())) {
        sb.append("    goto ").append(endLabel).append("\n");
    }
    
    // Else label
    sb.append(elseLabel).append(":\n");
    if (stmt.getElseStatements() != null) {
        for (Statement elseStmt : stmt.getElseStatements()) {
            generateStatement(program, elseStmt, sb);
        }
    }
    
    // Only add 'endLabel' if neither branch ends with a return
    if (!endsWithReturn(stmt.getThenStatements()) || !endsWithReturn(stmt.getElseStatements())) {
        // End label
        sb.append(endLabel).append(":\n");
    }
}

private boolean endsWithReturn(List<Statement> statements) {
    if (statements == null || statements.isEmpty()) {
        return false;
    }
    Statement lastStmt = statements.get(statements.size() - 1);
    if (lastStmt instanceof ReturnStatement) {
        return true;
    }
    // Optionally, check for nested blocks or other control flow statements
    return false;
}




    private void generateWhileStatement(Program program, WhileStatement stmt, StringBuilder sb) {
        String startLabel = getNextLabel();
        String endLabel = getNextLabel();

        // Loop start label
        sb.append(startLabel).append(":\n");
        
        // Generate condition
        generateExpression(stmt.getCondition(), sb);
        
        // If condition is false, exit loop
        sb.append("    ifeq ").append(endLabel).append("\n");
        
        // Generate loop body
        for (Statement bodyStmt : stmt.getBody()) {
            generateStatement(program, bodyStmt, sb);
        }
        
        // Jump back to start
        sb.append("    goto ").append(startLabel).append("\n");
        
        // End label
        sb.append(endLabel).append(":\n");
    }

    private void generateAssignment(Assignment stmt, StringBuilder sb) {
        if (stmt.getTarget().contains(".")) {  // Record field assignment
            String[] parts = stmt.getTarget().split("\\.");
            String recordName = parts[0];
            String fieldName = parts[1];
            
            // Create a FieldAssignment and generate code for it
            FieldAssignment fieldAssign = new FieldAssignment(
                recordName,
                fieldName,
                stmt.getValue(),
                globalVariableTypes.get(recordName)
            );
            generateFieldAssignment(fieldAssign, sb);
        } else if (stmt.getIndex() != null) {  // Array assignment
            String arrayName = stmt.getTarget();
            Type arrayType = variableTypes.get(arrayName);

            // Load array reference
            if (arrayType == null) {
                // Global array
                Type arrayGlobalType = globalVariableTypes.get(arrayName);
                if (arrayGlobalType == null) {
                    throw new RuntimeException("Undefined global variable: " + arrayName);
                }
                Type elementType = ((ArrayType) arrayGlobalType).getElementType();
                sb.append("    getstatic Main/").append(arrayName)
                  .append(" [").append(getArrayFieldDescriptor(elementType)).append("\n");
            } else {
                // Local array
                Integer arrayIndex = localVariables.get(arrayName);
                sb.append("    aload ").append(arrayIndex).append("\n");
            }

            // Generate index expression
            generateExpression(stmt.getIndex(), sb);
            sb.append("    iconst_1\n");
            sb.append("    isub\n"); // Adjust index for zero-based arrays

            // Generate value to store
            generateExpression(stmt.getValue(), sb);

            // Store value in array
            Type elementType = (arrayType != null)
                    ? ((ArrayType) arrayType).getElementType()
                    : ((ArrayType) globalVariableTypes.get(arrayName)).getElementType();
            if (elementType == Type.INTEGER || elementType == Type.BOOLEAN) {
                sb.append("    iastore\n");
            } else if (elementType instanceof SimpleType && 
                      ((SimpleType) elementType).getName().equals("real")) {
                sb.append("    dastore\n");
            }
        } else {  // Regular variable assignment
            generateExpression(stmt.getValue(), sb);
            String varName = stmt.getTarget();
            Integer varIndex = localVariables.get(varName);
            Type varType = variableTypes.get(varName);
            if (varIndex != null) {
                // Local variable
                generateStore(varType, varIndex, sb);
            } else {
                // Global variable
                Type globalVarType = globalVariableTypes.get(varName);
                if (globalVarType == null) {
                    throw new RuntimeException("Undefined global variable: " + varName);
                }
                String fieldDescriptor = getTypeDescriptor(globalVarType);
                sb.append("    putstatic Main/").append(varName).append(" ").append(fieldDescriptor).append("\n");
            }
        }
    }



    private void generateForLoop(Program program, ForLoop stmt, StringBuilder sb) {
    String startLabel = getNextLabel();
    String endLabel = getNextLabel();

    String varName = stmt.getVariable();
    int varIndex = nextLocalVariableIndex++;
    localVariables.put(varName, varIndex);
    variableTypes.put(varName, Type.INTEGER);

    // Initialize loop variable
    generateExpression(stmt.getRangeStart(), sb);
    sb.append("    istore ").append(varIndex).append("\n");

    // Start label
    sb.append(startLabel).append(":\n");

    // Load loop variable and end value
    sb.append("    iload ").append(varIndex).append("\n");
    generateExpression(stmt.getRangeEnd(), sb);

    if (stmt.isReverse()) {
        // For reverse loop: if i < endValue, exit loop
        sb.append("    if_icmplt ").append(endLabel).append("\n");
    } else {
        // For normal loop: if i > endValue, exit loop
        sb.append("    if_icmpgt ").append(endLabel).append("\n");
    }

    // Loop body
    for (Statement bodyStmt : stmt.getBody()) {
        generateStatement(program, bodyStmt, sb);
    }

    // Increment or decrement loop variable
    if (stmt.isReverse()) {
        sb.append("    iinc ").append(varIndex).append(" -1\n");
    } else {
        sb.append("    iinc ").append(varIndex).append(" 1\n");
    }

    // Jump back to start
    sb.append("    goto ").append(startLabel).append("\n");

    // End label
    sb.append(endLabel).append(":\n");
}


    private void generateArrayDecl(ArrayDecl decl, StringBuilder sb) {
        System.out.println("[DEBUG] Generating array declaration: " + decl.getName());
        ArrayType arrayType = (ArrayType) decl.getType();
        Type elementType = arrayType.getElementType();
        int size = arrayType.getSize();

        // Create array object
        String arrayTypeDesc = getArrayTypeDescriptor(elementType);
        sb.append("    bipush ").append(size).append("\n");
        sb.append("    newarray ").append(arrayTypeDesc).append("\n");

        // Store array reference in local variable
        int varIndex = nextIntVariable++;
        localVariables.put(decl.getName(), varIndex);
        variableTypes.put(decl.getName(), arrayType);
        sb.append("    astore ").append(varIndex).append("\n");
    }

    private String getArrayTypeDescriptor(Type elementType) {
    if (elementType == Type.INTEGER) {
        return "int";
    } else if (elementType == Type.BOOLEAN) {
        return "boolean";
    } else if (elementType instanceof SimpleType && 
              ((SimpleType) elementType).getName().equals("real")) {
        return "double";
    }
    throw new RuntimeException("Unsupported array element type: " + elementType);
}

    private void generateGlobalArrayField(ArrayDecl decl, StringBuilder sb) {
        String fieldDescriptor = getArrayFieldDescriptor(((ArrayType)decl.getType()).getElementType());
        sb.append(".field private static ").append(decl.getName())
          .append(" [").append(fieldDescriptor).append("\n");
    }

    private String getArrayFieldDescriptor(Type elementType) {
    if (elementType == Type.INTEGER) {
        return "I";
    } else if (elementType == Type.BOOLEAN) {
        return "Z";
    } else if (elementType instanceof SimpleType && 
              ((SimpleType) elementType).getName().equals("real")) {
        return "D";
    }
    throw new RuntimeException("Unsupported array element type: " + elementType);
}


    private void generateStaticInitializer(Program program, StringBuilder sb) {
    boolean hasArrays = false;
    for (Statement stmt : program.getStatements()) {
        if (stmt instanceof ArrayDecl) {
            hasArrays = true;
            break;
        }
    }

    if (!hasArrays) {
        return;
    }

    sb.append(".method static <clinit>()V\n");
    sb.append("    .limit stack 10\n");
    sb.append("    .limit locals 1\n\n");

    // Initialize arrays
    for (Statement stmt : program.getStatements()) {
        if (stmt instanceof ArrayDecl) {
            ArrayDecl arrayDecl = (ArrayDecl) stmt;
            ArrayType arrayType = (ArrayType) arrayDecl.getType();
            Type elementType = arrayType.getElementType();
            int size = arrayType.getSize();

            // Create array
            sb.append("    bipush ").append(size).append("\n");
            sb.append("    newarray ").append(getArrayTypeDescriptor(elementType)).append("\n");
            sb.append("    putstatic Main/").append(arrayDecl.getName())
              .append(" [").append(getArrayFieldDescriptor(elementType)).append("\n");
        }
    }

    sb.append("    return\n");
    sb.append(".end method\n\n");
}


    private void generateImplicitCast(Type sourceType, Type targetType, StringBuilder sb) {
    if (sourceType.equals(targetType)) {
        // No casting needed
        return;
    }
    if (sourceType == Type.INTEGER && targetType instanceof SimpleType &&
        ((SimpleType) targetType).getName().equals("real")) {
        sb.append("    i2d\n"); // Convert int to double
    } else if (sourceType instanceof SimpleType &&
               ((SimpleType) sourceType).getName().equals("real") &&
               targetType == Type.INTEGER) {
        sb.append("    d2i\n"); // Convert double to int
    } else {
        throw new RuntimeException("Unsupported implicit cast from " + sourceType + " to " + targetType);
    }
}


    // Helper method to calculate stack limit
    private int calculateStackLimit(RoutineDecl routine) {
        // Basic analysis of stack usage
        int maxStack = 0;
        int currentStack = 0;
        
        for (Statement stmt : routine.getBody()) {
            if (stmt instanceof PrintStatement) {
                PrintStatement printStmt = (PrintStatement) stmt;
                if (printStmt.getExpression() instanceof BinaryExpression) {
                    // StringBuilder + string concatenation operations
                    currentStack = 4;
                } else {
                    // getstatic + expression + invokevirtual
                    currentStack = 3;
                }
            } else if (stmt instanceof Assignment) {
                Assignment assign = (Assignment) stmt;
                if (assign.getValue() instanceof BinaryExpression) {
                    // Two operands + operation
                    currentStack = 2;
                } else {
                    currentStack = 1;
                }
            } else if (stmt instanceof VarDecl) {
                VarDecl varDecl = (VarDecl) stmt;
                if (varDecl.getInitializer() != null) {
                    currentStack = 1;
                }
            } else if (stmt instanceof ForLoop) {
                // Loop counter + comparison + branch
                currentStack = 2;
            }
            maxStack = Math.max(maxStack, currentStack);
        }
        
        return Math.max(maxStack, 10); // Minimum of 10 for safety
    }

    // Helper method to calculate locals limit
    private int calculateLocalsLimit(RoutineDecl routine) {
        // Count variables, accounting for doubles taking 2 slots
        int count = 1; // Start at 1 for args array
        for (Statement stmt : routine.getBody()) {
            if (stmt instanceof VarDecl) {
                VarDecl varDecl = (VarDecl) stmt;
                if (varDecl.getType() instanceof SimpleType && 
                    ((SimpleType)varDecl.getType()).getName().equals("real")) {
                    count += 2; // Double takes 2 slots
                } else {
                    count++;
                }
            }
        }
        return Math.max(count, 5); // Minimum of 5 for safety
    }

    private void generateRecordTypeClass(RecordTypeDecl recordType) {
        StringBuilder sb = new StringBuilder();
        
        // Generate class header
        sb.append(".class public ").append(recordType.getName()).append("\n");
        sb.append(".super java/lang/Object\n\n");
        
        // Generate fields
        for (VarDecl field : recordType.getFields()) {
            sb.append(".field public ")
              .append(field.getName())
              .append(" ")
              .append(getTypeDescriptor(field.getType()))
              .append("\n");
        }
        
        // Generate constructor
        sb.append("\n.method public <init>()V\n");
        sb.append("    .limit stack 1\n");
        sb.append("    .limit locals 1\n");
        sb.append("    aload_0\n");
        sb.append("    invokespecial java/lang/Object/<init>()V\n");
        sb.append("    return\n");
        sb.append(".end method\n");

        // Write to file in output directory
        try {
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }
            FileWriter writer = new FileWriter("output/" + recordType.getName() + ".j");
            writer.write(sb.toString());
            writer.close();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write record type class file", e);
        }
    }

    private void generateFieldAccess(FieldAccess fieldAccess, StringBuilder sb) {
        // Load the record instance
        sb.append("    getstatic Main/")
          .append(fieldAccess.getRecordName())
          .append(" L")
          .append(getRecordTypeName(fieldAccess.getRecordName()))
          .append(";\n");
          
        // Get the field
        sb.append("    getfield ")
          .append(getRecordTypeName(fieldAccess.getRecordName()))
          .append("/")
          .append(fieldAccess.getFieldName())
          .append(" ")
          .append(getTypeDescriptor(fieldAccess.getType()))
          .append("\n");
    }

    private void generateFieldAssignment(FieldAssignment assign, StringBuilder sb) {
        // Load the record instance
        sb.append("    getstatic Main/")
          .append(assign.getRecordName())
          .append(" L")
          .append(getRecordTypeName(assign.getRecordName()))
          .append(";\n");
          
        // Generate the value to be assigned
        generateExpression(assign.getValue(), sb);
        
        // Set the field
        sb.append("    putfield ")
          .append(getRecordTypeName(assign.getRecordName()))
          .append("/")
          .append(assign.getFieldName())
          .append(" ")
          .append(getTypeDescriptor(assign.getType()))
          .append("\n");
    }

    private String getRecordTypeName(String varName) {
        Type type = globalVariableTypes.get(varName);
        if (type instanceof SimpleType) {
            return ((SimpleType) type).getName();
        }
        throw new RuntimeException("Variable " + varName + " is not a record type");
    }
} 