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
        globalVariableTypes.clear();  // Clear global variables
        StringBuilder sb = new StringBuilder();

        // Generate record type classes first
        System.out.println("[DEBUG] Generating record type classes");
        for (Statement stmt : program.getStatements()) {
            System.out.println("[DEBUG] Processing statement: " + stmt.getClass().getSimpleName());
            if (stmt instanceof TypeDecl) {  // Changed from RecordTypeDecl to TypeDecl
                TypeDecl typeDecl = (TypeDecl) stmt;
                if (typeDecl.getType() instanceof RecordType) {
                    System.out.println("[DEBUG] Found record type declaration: " + typeDecl.getName());
                    generateRecordTypeClass(typeDecl);
                }
            }
        }

        // Class header
        sb.append(".class public Main\n");
        sb.append(".super java/lang/Object\n\n");

        // Generate global variable fields and store their types
        for (Statement stmt : program.getStatements()) {
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
                System.out.println("[DEBUG] Stored global variable: " + varDecl.getName() + 
                                 " with type: " + varDecl.getType());
            }
            if (stmt instanceof ArrayDecl) {
                generateGlobalArrayField((ArrayDecl) stmt, sb);
            }
        }

        // Default constructor
        sb.append(".method public <init>()V\n");
        sb.append("    aload_0\n");
        sb.append("    invokespecial java/lang/Object/<init>()V\n");
        sb.append("    return\n");
        sb.append(".end method\n\n");

        // Generate all routine declarations first
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl) {
                generateRoutineDecl(program, (RoutineDecl) stmt, sb);
            }
        }

        // Generate main method
        sb.append(".method public static main([Ljava/lang/String;)V\n");
        sb.append("    .limit stack 4\n");
        sb.append("    .limit locals 20\n\n");

        // Reset local variable counter for main method
        nextLocalVariableIndex = 1;  // Start at 1 because 0 is reserved for args array
        localVariables.clear();

        // Initialize record instances
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VarDecl) {
                VarDecl varDecl = (VarDecl) stmt;
                if (varDecl.getType() instanceof SimpleType) {
                    String typeName = ((SimpleType) varDecl.getType()).getName();
                    if (symbolTable.getTypeDefinition(typeName) instanceof RecordType) {
                        sb.append("    new ").append(typeName).append("\n");
                        sb.append("    dup\n");
                        sb.append("    invokespecial ").append(typeName).append("/<init>()V\n");
                        sb.append("    putstatic Main/").append(varDecl.getName())
                          .append(" L").append(typeName).append(";\n\n");
                    }
                }
            }
            if (stmt instanceof ArrayDecl) {
                ArrayDecl arrayDecl = (ArrayDecl) stmt;
                Type elementType = ((ArrayType) arrayDecl.getType()).getElementType();
                int size = ((ArrayType) arrayDecl.getType()).getSize();
                
                // Create array
                sb.append("    ; Initialize array ").append(arrayDecl.getName()).append("\n");
                sb.append("    bipush ").append(size).append("\n");
                sb.append("    newarray ");
                if (elementType == Type.INTEGER) {
                    sb.append("int\n");
                } else if (elementType == Type.BOOLEAN) {
                    sb.append("boolean\n");
                }
                sb.append("    putstatic Main/").append(arrayDecl.getName())
                  .append(" [").append(getTypeDescriptor(elementType)).append("\n\n");
            }
        }

        // Generate statements
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl && ((RoutineDecl) stmt).getName().equals("main")) {
                for (Statement bodyStmt : ((RoutineDecl) stmt).getBody()) {
                    generateStatement(program, bodyStmt, sb);
                }
            }
        }

        sb.append("\n    return\n");
        sb.append(".end method\n");

        return sb.toString();
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
        System.out.println("[DEBUG] Generating routine: " + routine.getName());
    // Clear local variables for new routine
    localVariables.clear();
    variableTypes.clear();
    nextLocalVariableIndex = 0; // Start at 0 for methods

    String methodName = routine.getName();
    Type returnType = routine.getReturnType();
    List<Parameter> params = routine.getParameters();

    // Generate method signature
    sb.append(".method public static ").append(methodName).append("(");
    for (Parameter param : params) {
        sb.append(getTypeDescriptor(param.getType()));
    }
    sb.append(")").append(getTypeDescriptor(returnType)).append("\n");

    // Calculate stack and locals limit (you may need to adjust these)
    sb.append("    .limit stack 20\n");
    sb.append("    .limit locals ").append(calculateLocalsLimit(routine)).append("\n\n");

    // Map parameters to local variables
    int paramIndex = 0;
    for (Parameter param : params) {
        localVariables.put(param.getName(), paramIndex);
        variableTypes.put(param.getName(), param.getType());
        paramIndex += getTypeSlotSize(param.getType());
    }
    nextLocalVariableIndex = paramIndex;

    // Generate routine body
    for (Statement stmt : routine.getBody()) {
        generateStatement(program, stmt, sb);
    }

    // Add return statement if needed
    if (returnType == null || returnType == Type.VOID) {
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
            // For record types, ensure we return the proper class descriptor
            Type typeDefinition = symbolTable.getTypeDefinition(typeName);
            if (typeDefinition instanceof RecordType) {
                return "L" + typeName + ";";
            }
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
        System.out.println("[DEBUG] Generating statement: " + stmt.getClass().getName());
        
        if (stmt instanceof VarDecl) {
            generateVarDecl((VarDecl) stmt, sb);
        } else if (stmt instanceof ArrayDecl) {
            generateArrayDecl((ArrayDecl) stmt, sb);
        } else if (stmt instanceof PrintStatement) {
            generatePrintStatement((PrintStatement) stmt, sb);
        } else if (stmt instanceof RoutineDecl) {
            generateRoutineDecl(program, (RoutineDecl) stmt, sb);
        } else if (stmt instanceof RoutineCallStatement) {
            System.out.println("[DEBUG] Found RoutineCallStatement");
            try {
                RoutineCallStatement routineCall = (RoutineCallStatement) stmt;
                System.out.println("[DEBUG] Successfully cast to RoutineCallStatement");
                System.out.println("[DEBUG] Routine name: " + routineCall.getName());
                
                // Create a RoutineCall from the RoutineCallStatement
                RoutineCall call = new RoutineCall(routineCall.getName(), routineCall.getArguments());
                generateRoutineCall(call, sb);
                
                RoutineDecl routine = symbolTable.getRoutine(routineCall.getName());
                System.out.println("[DEBUG] Found routine declaration: " + (routine != null));
                
                if (routine != null && routine.getReturnType() != Type.VOID) {
                    System.out.println("[DEBUG] Adding pop instruction for non-void return value");
                    sb.append("    pop\n");
                }
            } catch (Exception e) {
                System.err.println("[ERROR] Failed to process RoutineCallStatement: " + e.getMessage());
                e.printStackTrace();
            }
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
        int varIndex = nextLocalVariableIndex;
        
        // Increment the index by 2 for doubles, 1 for other types
        if (type instanceof SimpleType && ((SimpleType)type).getName().equals("real")) {
            nextLocalVariableIndex += 2;  // Doubles take up 2 slots
        } else {
            nextLocalVariableIndex++;     // Other types take 1 slot
        }
        
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
            sb.append("    dstore ").append(varIndex).append("\n");  // Changed from fstore to dstore
        }
    }

    private void generateLoad(Type type, int varIndex, StringBuilder sb) {
        if (type == Type.INTEGER || type == Type.BOOLEAN) {
            sb.append("    iload ").append(varIndex).append("\n");
        } else if (type instanceof SimpleType && ((SimpleType)type).getName().equals("real")) {
            sb.append("    dload ").append(varIndex).append("\n");  // Changed from fload to dload
        }
    }

    private void generatePrintStatement(PrintStatement stmt, StringBuilder sb) {
        Expression expr = stmt.getExpression();
        Type exprType = getExpressionType(expr);
        
        // First generate the PrintStream reference
        sb.append("    getstatic java/lang/System/out Ljava/io/PrintStream;\n");
        
        // Generate the expression code to put the value on the stack
        generateExpression(expr, sb);
        
        // Choose the appropriate println method based on the type
        if (expr instanceof RecordAccess) {
            RecordAccess access = (RecordAccess) expr;
            String recordName = access.getRecord();
            String fieldName = access.getField();
            
            // Get the field type from the record type
            Type recordType = globalVariableTypes.get(recordName);
            Type fieldType = ((RecordType)symbolTable.getTypeDefinition(
                ((SimpleType)recordType).getName())).getFields().get(fieldName);
            
            // Use the correct descriptor based on the field type
            if (fieldType == Type.INTEGER) {
                sb.append("    invokevirtual java/io/PrintStream/println(I)V\n");
            } else if (fieldType == Type.STRING) {
                sb.append("    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
            } else if (fieldType == Type.BOOLEAN) {
                sb.append("    invokevirtual java/io/PrintStream/println(Z)V\n");
            }
        } else {
            // Handle non-record expressions
            if (exprType == Type.INTEGER) {
                sb.append("    invokevirtual java/io/PrintStream/println(I)V\n");
            } else if (exprType == Type.STRING) {
                sb.append("    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
            } else if (exprType == Type.BOOLEAN) {
                sb.append("    invokevirtual java/io/PrintStream/println(Z)V\n");
            } else if (exprType instanceof SimpleType && 
                      ((SimpleType)exprType).getName().equals("real")) {
                sb.append("    invokevirtual java/io/PrintStream/println(D)V\n");
            }
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
        if (expr instanceof RecordAccess) {
            RecordAccess access = (RecordAccess) expr;
            String recordName = access.getRecord();
            String fieldName = access.getField();
            Type recordType = globalVariableTypes.get(recordName);
            return ((RecordType)symbolTable.getTypeDefinition(
                ((SimpleType)recordType).getName())).getFields().get(fieldName);
        }
        return Type.INTEGER; // default
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
            sb.append("    ldc2_w ").append(value).append("\n");  // Changed from ldc to ldc2_w for doubles
        } else if (expr instanceof BooleanLiteral) {
            sb.append("    iconst_").append(((BooleanLiteral) expr).getValue() ? "1" : "0").append("\n");
        } else if (expr instanceof StringLiteral) {
            sb.append("    ldc \"").append(((StringLiteral) expr).getValue()).append("\"\n");
        } else if (expr instanceof VariableReference) {
            String varName = ((VariableReference) expr).getName();
            Integer varIndex = localVariables.get(varName);
            Type varType = variableTypes.get(varName);
            if (varIndex != null) {
                generateLoad(varType, varIndex, sb);
            } else {
                // Check if it's a global variable
                varType = globalVariableTypes.get(varName);
                if (varType == null) {
                    throw new RuntimeException("Undefined variable: " + varName);
                }
                // Load global variable
                String fieldDescriptor = getTypeDescriptor(varType);
                sb.append("    getstatic Main/").append(varName).append(" ").append(fieldDescriptor).append("\n");
            }
        } else if (expr instanceof TypeCast) {
            generateTypeCast((TypeCast) expr, sb);
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
        } else if (expr instanceof RecordAccess) {
            // Update to use the existing RecordAccess class methods
            RecordAccess access = (RecordAccess) expr;
            String recordName = access.getRecord(); // Changed from getRecordName()
            String fieldName = access.getField();   // Changed from getFieldName()
            
            // Load the record reference
            sb.append("    getstatic Main/").append(recordName)
              .append(" L").append(getRecordTypeName(recordName)).append(";\n");
            
            // Get the field type
            Type recordType = globalVariableTypes.get(recordName);
            Type fieldType = ((RecordType)symbolTable.getTypeDefinition(
                ((SimpleType)recordType).getName())).getFields().get(fieldName);
            
            // Get the field value
            sb.append("    getfield ").append(getRecordTypeName(recordName))
              .append("/").append(fieldName)
              .append(" ").append(getTypeDescriptor(fieldType)).append("\n");
        } else if (expr instanceof UnaryExpression) {
            UnaryExpression unary = (UnaryExpression) expr;
            if (unary.getOperator().equals("not")) {
                generateExpression(unary.getExpression(), sb);
                // Negate the boolean value
                String label = getNextLabel();
                sb.append("    ifeq ").append(label).append("_true\n");
                sb.append("    iconst_0\n");
                sb.append("    goto ").append(label).append("_end\n");
                sb.append(label).append("_true:\n");
                sb.append("    iconst_1\n");
                sb.append(label).append("_end:\n");
            }
        } else if (expr instanceof BinaryExpression) {
            BinaryExpression binary = (BinaryExpression) expr;
            String op = binary.getOperator();
            
            if (op.equals("and") || op.equals("or") || op.equals("xor")) {
                generateLogicalOperation(binary, sb);
            } else if (op.equals("=")) {
                // Special handling for equality comparison
                generateExpression(binary.getLeft(), sb);
                generateExpression(binary.getRight(), sb);
                String label = getNextLabel();
                sb.append("    if_icmpeq ").append(label).append("_true\n");
                sb.append("    iconst_0\n");
                sb.append("    goto ").append(label).append("_end\n");
                sb.append(label).append("_true:\n");
                sb.append("    iconst_1\n");
                sb.append(label).append("_end:\n");
            } else {
                // Generate code for left and right operands
                generateExpression(binary.getLeft(), sb);
                generateExpression(binary.getRight(), sb);

                // Generate the operation
                switch (op) {
                    case "+": sb.append("    iadd\n"); break;
                    case "-": sb.append("    isub\n"); break;
                    case "*": sb.append("    imul\n"); break;
                    case "/": sb.append("    idiv\n"); break;
                    case "%": sb.append("    irem\n"); break;
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
                        throw new RuntimeException("Unknown operator: " + op);
                }
            }
        }
    }

    private void generateLogicalOperation(BinaryExpression expr, StringBuilder sb) {
        String op = expr.getOperator();
        
        if (op.equals("and")) {
            // Generate short-circuit AND
            String endLabel = getNextLabel();
            String falseLabel = getNextLabel();
            
            generateExpression(expr.getLeft(), sb);
            sb.append("    ifeq ").append(falseLabel).append("\n");
            generateExpression(expr.getRight(), sb);
            sb.append("    ifeq ").append(falseLabel).append("\n");
            sb.append("    iconst_1\n");
            sb.append("    goto ").append(endLabel).append("\n");
            sb.append(falseLabel).append(":\n");
            sb.append("    iconst_0\n");
            sb.append(endLabel).append(":\n");
        } 
        else if (op.equals("or")) {
            // Generate short-circuit OR
            String endLabel = getNextLabel();
            String trueLabel = getNextLabel();
            
            generateExpression(expr.getLeft(), sb);
            sb.append("    ifne ").append(trueLabel).append("\n");
            generateExpression(expr.getRight(), sb);
            sb.append("    ifne ").append(trueLabel).append("\n");
            sb.append("    iconst_0\n");
            sb.append("    goto ").append(endLabel).append("\n");
            sb.append(trueLabel).append(":\n");
            sb.append("    iconst_1\n");
            sb.append(endLabel).append(":\n");
        }
        else if (op.equals("xor")) {
            // Generate XOR
            generateExpression(expr.getLeft(), sb);
            generateExpression(expr.getRight(), sb);
            sb.append("    ixor\n");
        }
    }

    private void generateTypeCastAndStore(Type sourceType, Type targetType, int varIndex, StringBuilder sb) {
        if (sourceType == Type.INTEGER && targetType instanceof SimpleType && 
            ((SimpleType)targetType).getName().equals("real")) {
            sb.append("    i2d\n");  // Changed from i2f to i2d
            sb.append("    dstore ").append(varIndex).append("\n");  // Changed from fstore to dstore
        } else if (sourceType instanceof SimpleType && 
                  ((SimpleType)sourceType).getName().equals("real") && 
                  targetType == Type.INTEGER) {
            sb.append("    d2i\n");  // Changed from f2i to d2i
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
            sb.append("    dconst_0\n");  // Changed from fconst_0 to dconst_0
            sb.append("    dcmpl\n");     // Changed from fcmpl to dcmpl
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
            
            Type recordType = globalVariableTypes.get(recordName);
            if (!(recordType instanceof SimpleType)) {
                throw new RuntimeException("Expected record type for " + recordName);
            }
            
            RecordType actualRecordType = (RecordType) symbolTable.getTypeDefinition(((SimpleType) recordType).getName());
            Type fieldType = actualRecordType.getFields().get(fieldName);
            
            // Create a FieldAssignment and generate code for it
            FieldAssignment fieldAssign = new FieldAssignment(
                recordName,
                fieldName,
                stmt.getValue(),
                fieldType  // Pass the correct field type
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
        Type elementType = ((ArrayType) decl.getType()).getElementType();
        String fieldDescriptor = "[" + getTypeDescriptor(elementType);
        sb.append(".field private static ")
          .append(decl.getName())
          .append(" ")
          .append(fieldDescriptor)
          .append("\n\n");
        
        // Store the type in globalVariableTypes
        globalVariableTypes.put(decl.getName(), decl.getType());
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

    private void generateRecordTypeClass(TypeDecl typeDecl) {
        System.out.println("[DEBUG] Generating record type class: " + typeDecl.getName());
        StringBuilder sb = new StringBuilder();
        
        // Generate class header
        sb.append(".class public ").append(typeDecl.getName()).append("\n");
        sb.append(".super java/lang/Object\n\n");
        
        // Generate fields
        RecordType recordType = (RecordType) typeDecl.getType();
        Map<String, Type> fields = recordType.getFields();
        for (Map.Entry<String, Type> field : fields.entrySet()) {
            String fieldDescriptor = getTypeDescriptor(field.getValue());
            System.out.println("[DEBUG] Adding field: " + field.getKey() + " with descriptor: " + fieldDescriptor);
            sb.append(".field public ")
              .append(field.getKey())
              .append(" ")
              .append(fieldDescriptor)
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
        System.out.println("[DEBUG] Writing record type class file");
        try {
            File outputDir = new File("output");
            if (!outputDir.exists()) {
                outputDir.mkdirs();
                System.out.println("[DEBUG] Created output directory: " + outputDir.getAbsolutePath());
            }
            String filePath = outputDir + "/" + typeDecl.getName() + ".j";
            System.out.println("[DEBUG] Writing to file: " + filePath);
            try (FileWriter writer = new FileWriter(filePath)) {
                writer.write(sb.toString());
                System.out.println("[DEBUG] Successfully wrote record type class file");
                System.out.println("[DEBUG] File contents:\n" + sb.toString());
            }
        } catch (IOException e) {
            System.err.println("[DEBUG] Failed to write record type class file: " + e.getMessage());
            e.printStackTrace();
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
        String recordTypeName = getRecordTypeName(assign.getRecordName());
        sb.append("    getstatic Main/")
          .append(assign.getRecordName())
          .append(" L")
          .append(recordTypeName)
          .append(";\n");
          
        // Generate the value to be assigned
        generateExpression(assign.getValue(), sb);
        
        // Ensure proper type conversion if needed
        Type fieldType = assign.getType();
        Type valueType = getExpressionType(assign.getValue());
        generateImplicitCast(valueType, fieldType, sb);
        
        // Set the field
        sb.append("    putfield ")
          .append(recordTypeName)
          .append("/")
          .append(assign.getFieldName())
          .append(" ")
          .append(getTypeDescriptor(fieldType))
          .append("\n");
    }

    private String getRecordTypeName(String varName) {
        Type type = globalVariableTypes.get(varName);
        System.out.println("[DEBUG] Getting record type for: " + varName + ", type: " + type);
        
        if (type == null) {
            throw new RuntimeException("Variable not found: " + varName);
        }
        
        if (type instanceof SimpleType) {
            String typeName = ((SimpleType) type).getName();
            System.out.println("[DEBUG] Found record type name: " + typeName);
            return typeName;
        }
        throw new RuntimeException("Variable " + varName + " is not a record type");
    }
}
