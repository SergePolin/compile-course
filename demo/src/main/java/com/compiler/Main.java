package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.util.List;
import jasmin.ClassFile;
import java.io.*;

public class Main {
    public static void main(String[] args) {
        // Specify the input file path
        String inputFilePath = "src/main/resources/test.imp";
        String outputPath = "output";  // Directory for generated class files

        try {
            // Set up the lexer and parser
            ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
            Lexer lexer = new Lexer(new FileReader(inputFilePath), symbolFactory);
            ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);

            // Parse the input file
            Symbol parseTree = parser.parse();
            Program program = (Program) parseTree.value;

            // Add this after parsing but before semantic analysis
            System.out.println("Parsed successfully!");

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

            System.out.println("Semantic analysis completed successfully!");

            // Generate Jasmin assembly code
            JasminCodeGenerator codeGen = new JasminCodeGenerator(analyzer.getSymbolTable());
            String jasminCode = codeGen.generate(program);

            // Create output directory if it doesn't exist
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Write Jasmin assembly to file
            String jasminFile = outputPath + "/Main.j";
            try (FileWriter writer = new FileWriter(jasminFile)) {
                writer.write(jasminCode);
            }

            // Assemble the Jasmin code into a class file
            ClassFile classFile = new ClassFile();
            classFile.readJasmin(new FileInputStream(jasminFile), "Main", false);
            String classFileName = outputPath + "/Main.class";
            try (FileOutputStream fos = new FileOutputStream(classFileName)) {
                classFile.write(fos);
            }

            System.out.println("Compilation completed successfully!");
            System.out.println("Generated class file: " + classFileName);

            // Run the compiled program
            System.out.println("\nRunning the compiled program:");
            System.out.println("----------------------------");
            
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputPath, "Main");
            pb.inheritIO(); // Redirect program output to console
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            System.out.println("----------------------------");
            System.out.println("Program finished with exit code: " + exitCode);

            // Print the AST
            System.out.println("\nAbstract Syntax Tree:");
            System.out.println(program);

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }
}
