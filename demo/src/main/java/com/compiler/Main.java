package com.compiler;

import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;

public class Main {

    public static void main(String[] args) {
        try {
            // Load the test file (you can modify the path as needed)
            FileReader fileReader = new FileReader("src/main/resources/test.imp");

            // Create a ComplexSymbolFactory
            ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();

            // Initialize the lexer with the file reader and symbol factory
            Lexer lexer = new Lexer(fileReader, symbolFactory);

            // Initialize the parser with the lexer and symbol factory
            ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);

            // Parse the input and obtain the root of the AST (Program)
            Symbol parseResult = parser.parse();
            Program program = (Program) parseResult.value;

            // Print the resulting AST
            System.out.println("Parsed AST:");
            System.out.println(program);

        } catch (FileNotFoundException e) {
            System.err.println("File not found: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("An error occurred during parsing: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
