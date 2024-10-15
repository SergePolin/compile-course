package src;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java_cup.runtime.Symbol; // Import Symbol from CUP runtime

public class Main {
    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Main <input-file>");
            return;
        }

        try {
            String input = new String(Files.readAllBytes(Paths.get(args[0])));
            Lexer lexer = new Lexer(input);

            // Print tokens (for debugging)
            System.out.println("Tokens:");
            for (Lexer.Token token : lexer.tokenize()) {
                System.out.println(token);
            }

            // Create a new lexer for the parser
            Lexer parserLexer = new Lexer(input);
            Parser parser = new Parser(parserLexer);

            try {
                Symbol result = parser.parse();
                if (result != null && result.value != null) {
                    Program program = (Program) result.value;
                    System.out.println("Parsed Program:");
                    System.out.println(program);
                    processAST(program);
                } else {
                    System.out.println("Parsing completed successfully, but no program was produced.");
                }
            } catch (Exception e) {
                System.err.println("Parsing error: " + e.getMessage());
                e.printStackTrace();
            }
        } catch (IOException e) {
            System.err.println("Error reading input file: " + e.getMessage());
        }
    }

    private static void processAST(Program program) {
        System.out.println("Processing AST:");
        for (Declaration decl : program.declarations) {
            if (decl instanceof RoutineDeclaration) {
                processRoutine((RoutineDeclaration) decl);
            } else if (decl instanceof TypeDeclaration) {
                processType((TypeDeclaration) decl);
            } else if (decl instanceof VarDeclaration) {
                processVar((VarDeclaration) decl);
            } else if (decl instanceof Statement) {
                processStatement((Statement) decl);
            }
        }
    }

    private static void processStatement(Statement stmt) {
        if (stmt instanceof ExpressionStatement) {
            processExpression(((ExpressionStatement) stmt).expression);
        } else if (stmt instanceof AssignmentStatement) {
            // Handle AssignmentStatement
        } else if (stmt instanceof IfStatement) {
            // Handle IfStatement
        }
        // Add other statement types as needed
    }

    private static void processExpression(Expression expr) {
        System.out.println("Expression: " + expr);
    }

    private static void processRoutine(RoutineDeclaration routine) {
        System.out.println("Routine: " + routine.name);
        // Process parameters and body
    }

    private static void processType(TypeDeclaration type) {
        System.out.println("Type: " + type.name);
        // Process fields
    }

    private static void processVar(VarDeclaration var) {
        System.out.println("Variable: " + var.name + " : " + var.type);
        // Process initial value if present
    }
}
