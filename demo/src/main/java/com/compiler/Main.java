package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        // Specify the input file path
        String inputFilePath = "src/main/resources/test.imp"; // Update this path

        try {
            // Set up the lexer and parser
            ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
            Lexer lexer = new Lexer(new FileReader(inputFilePath), symbolFactory);
            ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);

            // Parse the input file
            Symbol parseTree = parser.parse();
            Program program = (Program) parseTree.value;

            // Perform semantic analysis
            SemanticAnalyzer analyzer = new SemanticAnalyzer();
            List<SemanticError> errors = analyzer.analyze(program);

            // Check for semantic errors
            if (!errors.isEmpty()) {
                System.err.println("Semantic errors found:");
                for (SemanticError error : errors) {
                    System.err.println(error);
                }
                System.exit(1);
            }

            // Print the AST
            System.out.println("Abstract Syntax Tree:");
            System.out.println(program);

            // TODO: Execute the program or generate code
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
