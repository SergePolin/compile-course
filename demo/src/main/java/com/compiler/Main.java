package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.util.List;
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

            // Create output directory if it doesn't exist
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Generate Jasmin assembly code
            JasminCodeGenerator codeGen = new JasminCodeGenerator(analyzer.getSymbolTable());
            String jasminCode = codeGen.generate(program);

            // Write Main.j assembly to file
            String mainJasminFile = outputPath + "/Main.j";
            try (FileWriter writer = new FileWriter(mainJasminFile)) {
                writer.write(jasminCode);
            }

            // print Main.j code
            System.out.println("\nGenerated Jasmin code:");
            System.out.println("----------------------------");
            System.out.println(jasminCode);
            System.out.println("----------------------------\n");

            // Compile all .j files in the output directory
            File[] jasminFiles = outputDir.listFiles((dir, name) -> name.endsWith(".j"));
            if (jasminFiles != null) {
                // First compile record type files
                for (File jasminFile : jasminFiles) {
                    if (!jasminFile.getName().equals("Main.j")) {
                        compileJasminFile(jasminFile.getPath(), outputPath);
                    }
                }
                
                // Then compile Main.j
                compileJasminFile(mainJasminFile, outputPath);
            }

            // Run the compiled program
            System.out.println("\nRunning the compiled program:");
            System.out.println("----------------------------");
            
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputPath, "Main");
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            System.out.println("----------------------------");
            System.out.println("Program finished with exit code: " + exitCode);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void compileJasminFile(String jasminFile, String outputDir) {
        try {
            // Use ProcessBuilder to run jasmin.jar
            ProcessBuilder pb = new ProcessBuilder(
                "java", 
                "-jar", 
                "lib/jasmin.jar", 
                "-d", 
                outputDir, 
                jasminFile
            );
            
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (exitCode == 0) {
                System.out.println("Successfully compiled " + jasminFile);
            } else {
                System.err.println("Error compiling " + jasminFile + ". Exit code: " + exitCode);
                
                // Print error output if any
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error compiling " + jasminFile + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}
