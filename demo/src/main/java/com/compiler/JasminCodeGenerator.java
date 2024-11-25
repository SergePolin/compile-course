package com.compiler;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.compiler.semantic.SymbolTable;

public class JasminCodeGenerator {
    private int labelCounter = 0;
    private Map<String, Integer> localVariables = new HashMap<>();
    private Map<String, Type> variableTypes = new HashMap<>();
    private int nextIntVariable = 1;
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

        // Class header
        sb.append(".class public Main\n");
        sb.append(".super java/lang/Object\n\n");

        // Generate static field declarations for global variables
        for (Statement stmt : program.getStatements()) {
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

        // Static initializer for global arrays
        generateStaticInitializer(program, sb);

        // First generate all routine declarations except main
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl && !((RoutineDecl) stmt).getName().equals("main")) {
                generateRoutineDecl((RoutineDecl) stmt, sb);
            }
        }

        // Then generate the main method
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof RoutineDecl && ((RoutineDecl) stmt).getName().equals("main")) {
                // Generate special main method wrapper
                sb.append(".method public static main([Ljava/lang/String;)V\n");
                sb.append("    .limit stack 100\n");
                sb.append("    .limit locals 100\n\n");
                
                // Call the actual main routine
                sb.append("    invokestatic Main/main()V\n");
                sb.append("    return\n");
                sb.append(".end method\n\n");

                // Generate the actual main routine
                generateRoutineDecl((RoutineDecl) stmt, sb);
            }
        }

        String result = sb.toString();
        System.out.println("[DEBUG] Generated Jasmin code:\n" + result);
        return result;
    }

    private void generateRoutineDecl(RoutineDecl routine, StringBuilder sb) {
        // Clear local variables for new routine
        localVariables.clear();
        variableTypes.clear();
        nextIntVariable = 0;
        nextDoubleVariable = 1;

        String methodName = routine.getName();
        Type returnType = routine.getReturnType();
        List<Parameter> params = routine.getParameters();

        // Generate method signature
        sb.append(".method public static ").append(methodName).append("(");
        
        // Add parameter types to signature
        for (Parameter param : params) {
            if (param.getType() == Type.INTEGER || param.getType() == Type.BOOLEAN) {
                sb.append("I");
            } else if (param.getType() instanceof SimpleType && 
                      ((SimpleType)param.getType()).getName().equals("real")) {
                sb.append("D");
            }
        }
        sb.append(")");
        
        // Add return type to signature
        if (returnType == Type.INTEGER || returnType == Type.BOOLEAN) {
            sb.append("I\n");
        } else if (returnType instanceof SimpleType && 
                  ((SimpleType)returnType).getName().equals("real")) {
            sb.append("D\n");
        } else {
            sb.append("V\n");
        }

        // Method prologue
        sb.append("    .limit stack 100\n");
        sb.append("    .limit locals 100\n\n");

        // Register parameters in local variables table
        int paramIndex = 0;
        for (Parameter param : params) {
            localVariables.put(param.getName(), paramIndex);
            variableTypes.put(param.getName(), param.getType());
            paramIndex++;
            if (param.getType() instanceof SimpleType && 
                ((SimpleType)param.getType()).getName().equals("real")) {
                paramIndex++; // doubles take 2 slots
            }
        }

        // Generate routine body
        for (Statement stmt : routine.getBody()) {
            generateStatement(stmt, sb);
        }

        // Add default return if none present
        if (returnType == null) {
            sb.append("    return\n");
        }

        sb.append(".end method\n\n");
    }

    private void generateStatement(Statement stmt, StringBuilder sb) {
        if (stmt instanceof VarDecl) {
            generateVarDecl((VarDecl) stmt, sb);
        } else if (stmt instanceof ArrayDecl) {
            generateArrayDecl((ArrayDecl) stmt, sb);
        } else if (stmt instanceof PrintStatement) {
            generatePrintStatement((PrintStatement) stmt, sb);
        } else if (stmt instanceof RoutineDecl) {
            generateRoutineDecl((RoutineDecl) stmt, sb);
        } else if (stmt instanceof IfStatement) {
            generateIfStatement((IfStatement) stmt, sb);
        } else if (stmt instanceof WhileStatement) {
            generateWhileStatement((WhileStatement) stmt, sb);
        } else if (stmt instanceof Assignment) {
            generateAssignment((Assignment) stmt, sb);
        } else if (stmt instanceof ForLoop) {
            generateForLoop((ForLoop) stmt, sb);
        }
    }

    private void generateVarDecl(VarDecl decl, StringBuilder sb) {
        System.out.println("[DEBUG] Generating variable declaration: " + decl.getName());
        Type type = decl.getType();
        int varIndex;
        
        // Allocate variable slot based on type
        if (type instanceof SimpleType && ((SimpleType)type).getName().equals("real")) {
            // Make sure doubles are aligned on even indices
            if (nextIntVariable % 2 != 0) {
                nextIntVariable++;
            }
            varIndex = nextIntVariable;
            nextIntVariable += 2;  // Doubles take 2 slots
            System.out.println("[DEBUG] Allocated double variable at index: " + varIndex);
        } else {
            // For int and boolean, use separate counter
            varIndex = nextIntVariable++;
            System.out.println("[DEBUG] Allocated int/bool variable at index: " + varIndex);
        }
        
        localVariables.put(decl.getName(), varIndex);
        variableTypes.put(decl.getName(), type);

        if (decl.getInitializer() != null) {
            generateExpression(decl.getInitializer(), sb);
            if (decl.getInitializer() instanceof TypeCast) {
                generateTypeCastStore(decl.getType(), ((TypeCast) decl.getInitializer()).getTargetType(), varIndex, sb);
            } else {
                generateStore(type, varIndex, sb);
            }
        }
    }

    private void generateStore(Type type, int varIndex, StringBuilder sb) {
        System.out.println("[DEBUG] Generating store for type " + type + " at index " + varIndex);
        if (type == Type.INTEGER || type == Type.BOOLEAN) {
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (type instanceof SimpleType && ((SimpleType)type).getName().equals("real")) {
            sb.append("    dstore ").append(varIndex).append("\n");
        }
    }

    private void generateLoad(Type type, int varIndex, StringBuilder sb) {
        System.out.println("[DEBUG] Generating load for type " + type + " at index " + varIndex);
        if (type == Type.INTEGER || type == Type.BOOLEAN) {
            sb.append("    iload ").append(varIndex).append("\n");
        } else if (type instanceof SimpleType && ((SimpleType)type).getName().equals("real")) {
            sb.append("    dload ").append(varIndex).append("\n");
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
                  ((SimpleType)exprType).getName().equals("real")) {
            sb.append("    invokevirtual java/io/PrintStream/println(D)V\n");
        } else if (exprType == Type.BOOLEAN) {
            sb.append("    invokevirtual java/io/PrintStream/println(Z)V\n");
        } else {
            sb.append("    invokevirtual java/io/PrintStream/println(Ljava/lang/String;)V\n");
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
            } else {
                sb.append("    ldc ").append(value).append("\n");
            }
        } else if (expr instanceof RealLiteral) {
            double value = ((RealLiteral) expr).getValue();
            sb.append("    ldc2_w ").append(value).append("d\n");
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
            Integer arrayIndex = localVariables.get(arrayName);
            Type arrayType = variableTypes.get(arrayName);

            // Load array reference
            sb.append("    aload ").append(arrayIndex).append("\n");

            // Generate index expression
            generateExpression(access.getIndex(), sb);

            // Load array element
            Type elementType = ((ArrayType)arrayType).getElementType();
            if (elementType == Type.INTEGER || elementType == Type.BOOLEAN) {
                sb.append("    iaload\n");
            } else if (elementType instanceof SimpleType && 
                      ((SimpleType)elementType).getName().equals("real")) {
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

    private void generateTypeCast(TypeCast cast, StringBuilder sb) {
        generateExpression(cast.getExpression(), sb);
        Type sourceType = getExpressionType(cast.getExpression());
        Type targetType = cast.getTargetType();

        System.out.println("[DEBUG] Generating type cast from " + sourceType + " to " + targetType);

        if (sourceType == Type.INTEGER && targetType instanceof SimpleType && 
            ((SimpleType)targetType).getName().equals("real")) {
            sb.append("    i2d\n");
        } else if (sourceType instanceof SimpleType && 
                  ((SimpleType)sourceType).getName().equals("real") && 
                  targetType == Type.INTEGER) {
            sb.append("    d2i\n");
        } else if (sourceType == Type.BOOLEAN && targetType == Type.INTEGER) {
            // Boolean to int - no conversion needed, already 0 or 1
        } else if (sourceType == Type.INTEGER && targetType == Type.BOOLEAN) {
            // Int to boolean - compare with 0
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("_false\n");
            sb.append("    iconst_1\n");
            sb.append("    goto ").append(label).append("_end\n");
            sb.append(label).append("_false:\n");
            sb.append("    iconst_0\n");
            sb.append(label).append("_end:\n");
        } else if (sourceType instanceof SimpleType && 
                  ((SimpleType)sourceType).getName().equals("real") && 
                  targetType == Type.BOOLEAN) {
            // Real to boolean - convert to int first
            sb.append("    d2i\n");
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("_false\n");
            sb.append("    iconst_1\n");
            sb.append("    goto ").append(label).append("_end\n");
            sb.append(label).append("_false:\n");
            sb.append("    iconst_0\n");
            sb.append(label).append("_end:\n");
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

    private void generateTypeCastStore(Type type, Type targetType, int varIndex, StringBuilder sb) {
        System.out.println("[DEBUG] Generating type cast store for type " + type + " to " + targetType + " at index " + varIndex);
        
        if (type == Type.INTEGER && targetType instanceof SimpleType && 
            ((SimpleType)targetType).getName().equals("real")) {
            sb.append("    i2d\n");
            sb.append("    dstore ").append(varIndex).append("\n");
        } else if (type instanceof SimpleType && 
                  ((SimpleType)type).getName().equals("real") && 
                  targetType == Type.INTEGER) {
            sb.append("    d2i\n");
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (type == Type.BOOLEAN && targetType == Type.INTEGER) {
            // Boolean to int - no conversion needed, already 0 or 1
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (type == Type.INTEGER && targetType == Type.BOOLEAN) {
            // Int to boolean - compare with 0
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("_false\n");
            sb.append("    iconst_1\n");
            sb.append("    goto ").append(label).append("_end\n");
            sb.append(label).append("_false:\n");
            sb.append("    iconst_0\n");
            sb.append(label).append("_end:\n");
            sb.append("    istore ").append(varIndex).append("\n");
        } else if (type instanceof SimpleType && 
                  ((SimpleType)type).getName().equals("real") && 
                  targetType == Type.BOOLEAN) {
            // Real to boolean - convert to int first, then compare with 0
            sb.append("    d2i\n");  // Convert double to int
            String label = getNextLabel();
            sb.append("    ifeq ").append(label).append("_false\n");
            sb.append("    iconst_1\n");
            sb.append("    goto ").append(label).append("_end\n");
            sb.append(label).append("_false:\n");
            sb.append("    iconst_0\n");
            sb.append(label).append("_end:\n");
            sb.append("    istore ").append(varIndex).append("\n");
        } else {
            // Default case - just store the value
            generateStore(type, varIndex, sb);
        }
    }

    private void generateIfStatement(IfStatement stmt, StringBuilder sb) {
        String endLabel = getNextLabel();
        String elseLabel = getNextLabel();
        
        // Generate condition code
        generateExpression(stmt.getCondition(), sb);
        
        // If condition is false, jump to else block
        sb.append("    ifeq ").append(elseLabel).append("\n");
        
        // Generate then block
        for (Statement thenStmt : stmt.getThenStatements()) {
            generateStatement(thenStmt, sb);
        }
        
        // Jump to end (skip else block)
        if (stmt.getElseStatements() != null && !stmt.getElseStatements().isEmpty()) {
            sb.append("    goto ").append(endLabel).append("\n");
        }
        
        // Generate else block
        sb.append(elseLabel).append(":\n");
        if (stmt.getElseStatements() != null) {
            for (Statement elseStmt : stmt.getElseStatements()) {
                generateStatement(elseStmt, sb);
            }
        }
        
        // Only add end label if there was an else block
        if (stmt.getElseStatements() != null && !stmt.getElseStatements().isEmpty()) {
            sb.append(endLabel).append(":\n");
        }
    }

    private void generateWhileStatement(WhileStatement stmt, StringBuilder sb) {
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
            generateStatement(bodyStmt, sb);
        }
        
        // Jump back to start
        sb.append("    goto ").append(startLabel).append("\n");
        
        // End label
        sb.append(endLabel).append(":\n");
    }

    private void generateAssignment(Assignment stmt, StringBuilder sb) {
        if (stmt.getIndex() != null) {  // Array assignment
            // Load array reference
            String arrayName = stmt.getTarget();
            Type arrayType = variableTypes.get(arrayName);
            
            if (arrayType == null) {
                // Global array
                Type elementType = ((ArrayType)symbolTable.getType(arrayName)).getElementType();
                sb.append("    getstatic Main/").append(arrayName)
                  .append(" [").append(getArrayFieldDescriptor(elementType)).append("\n");
            } else {
                // Local array
                Integer arrayIndex = localVariables.get(arrayName);
                sb.append("    aload ").append(arrayIndex).append("\n");
            }

            // Generate array index
            generateExpression(stmt.getIndex(), sb);

            // Generate value to store
            generateExpression(stmt.getValue(), sb);

            // Store value in array
            Type elementType = ((ArrayType)arrayType).getElementType();
            if (elementType == Type.INTEGER || elementType == Type.BOOLEAN) {
                sb.append("    iastore\n");
            } else if (elementType instanceof SimpleType && 
                      ((SimpleType)elementType).getName().equals("real")) {
                sb.append("    dastore\n");
            }
        } else {  // Regular variable assignment
            generateExpression(stmt.getValue(), sb);
            String varName = stmt.getTarget();
            Integer varIndex = localVariables.get(varName);
            Type varType = variableTypes.get(varName);
            generateStore(varType, varIndex, sb);
        }
    }

    private void generateForLoop(ForLoop stmt, StringBuilder sb) {
        String startLabel = getNextLabel();
        String endLabel = getNextLabel();
        String incrementLabel = getNextLabel();
        
        // Get loop variable index
        String varName = stmt.getVariable();
        int varIndex = localVariables.get(varName);
        
        // Initialize loop variable with start value
        generateExpression(stmt.getRangeStart(), sb);
        sb.append("    istore ").append(varIndex).append("\n");
        
        // Loop start
        sb.append(startLabel).append(":\n");
        
        // Load and compare loop variable with end value
        sb.append("    iload ").append(varIndex).append("\n");
        generateExpression(stmt.getRangeEnd(), sb);

        // Check if it's a reverse loop
        if (stmt.getReverse() != null && stmt.getReverse().isReverse()) {
            // If loop variable < end value, exit loop
            sb.append("    if_icmplt ").append(endLabel).append("\n");
        } else {
            // If loop variable > end value, exit loop
            sb.append("    if_icmpgt ").append(endLabel).append("\n");
        }
        
        // Generate loop body
        for (Statement bodyStmt : stmt.getBody()) {
            generateStatement(bodyStmt, sb);
        }
        
        // Increment/Decrement loop variable
        sb.append(incrementLabel).append(":\n");
        sb.append("    iload ").append(varIndex).append("\n");
        sb.append("    iconst_1\n");
        
        if (stmt.getReverse() != null && stmt.getReverse().isReverse()) {
            // Decrement for reverse loop
            sb.append("    isub\n");
        } else {
            // Increment for forward loop
            sb.append("    iadd\n");
        }
        
        sb.append("    istore ").append(varIndex).append("\n");
        
        // Jump back to start
        sb.append("    goto ").append(startLabel).append("\n");
        
        // End of loop
        sb.append(endLabel).append(":\n");
    }

    private void generateRoutineCall(RoutineCall call, StringBuilder sb) {
        System.out.println("[DEBUG] Generating routine call: " + call.getName());
        
        // Generate code for arguments
        for (Expression arg : call.getArguments()) {
            generateExpression(arg, sb);
        }

        // Generate invocation
        sb.append("    invokestatic Main/").append(call.getName()).append("(");
        
        // Add parameter types to signature
        for (Expression arg : call.getArguments()) {
            Type argType = getExpressionType(arg);
            if (argType == Type.INTEGER || argType == Type.BOOLEAN) {
                sb.append("I");
            } else if (argType instanceof SimpleType && 
                      ((SimpleType)argType).getName().equals("real")) {
                sb.append("D");
            }
        }
        
        // Add return type
        RoutineDecl routine = symbolTable.getRoutine(call.getName());
        Type returnType = routine.getReturnType();
        sb.append(")");
        
        if (returnType == Type.INTEGER || returnType == Type.BOOLEAN) {
            sb.append("I");
        } else if (returnType instanceof SimpleType && 
                  ((SimpleType)returnType).getName().equals("real")) {
            sb.append("D");
        } else {
            sb.append("V");
        }
        sb.append("\n");
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
                  ((SimpleType)elementType).getName().equals("real")) {
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
                  ((SimpleType)elementType).getName().equals("real")) {
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
        sb.append("    .limit stack 100\n");
        sb.append("    .limit locals 100\n\n");

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
} 