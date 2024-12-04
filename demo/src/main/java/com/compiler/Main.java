package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.Symbol;
import java.io.FileReader;
import java.util.List;
import java.io.*;

/**
 * Main class for the Imperative Language Compiler.
 * This class serves as the entry point for the compiler, handling command-line arguments,
 * file processing, and orchestrating the compilation phases:
 * <ul>
 *   <li>Lexical Analysis - Tokenizing the input file</li>
 *   <li>Syntactic Analysis - Parsing tokens into an AST</li>
 *   <li>Semantic Analysis - Type checking and validation</li>
 *   <li>Code Generation - Producing JVM bytecode via Jasmin</li>
 * </ul>
 * The compiler produces JVM bytecode that can be executed on any Java Virtual Machine.
 */
public class Main {
    /** Flag to enable/disable debug output for detailed compilation information */
    private static boolean debug = false;
    /** Standard output stream for normal program output and compilation results */
    private static PrintStream out = System.out;
    /** Error output stream for error messages, warnings, and debug information */
    private static PrintStream err = System.err;

    /**
     * Main entry point for the compiler.
     * Processes command line arguments, reads the input file, and executes all compilation phases.
     * If compilation is successful, the program will be executed immediately.
     *
     * @param args Command line arguments:
     *             args[0] - Input file path (required) - Path to the source code file
     *             args[1] - Optional "--debug" flag for detailed compilation output
     */
    public static void main(String[] args) {
        if (args.length < 1) {
            err.println("Please provide an input file path");
            err.println("Usage: java -jar imperativeLangParser.jar <input-file> [--debug]");
            System.exit(1);
        }

        // Get input file path from command line argument
        String inputFilePath = args[0];
        String outputPath = "output";  // Directory for generated class files

        // Check for debug flag
        if (args.length > 1 && args[1].equals("--debug")) {
            debug = true;
        }

        try {
            // Verify input file exists
            File inputFile = new File(inputFilePath);
            if (!inputFile.exists()) {
                err.println("Input file not found: " + inputFilePath);
                System.exit(1);
            }

            // Set up the lexer and parser
            ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
            Lexer lexer = new Lexer(new FileReader(inputFilePath), symbolFactory);
            ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);

            // Parse the input file
            Symbol parseTree = parser.parse();
            Program program = (Program) parseTree.value;

            if (debug) {
                err.println("Parsed successfully!");
                
                err.println("\nAbstract Syntax Tree:");
                err.println("----------------------------");
                err.println(program.toString());
                err.println("----------------------------\n");
            }

            // Perform semantic analysis
            SemanticAnalyzer analyzer = new SemanticAnalyzer(debug);
            List<SemanticError> errors = analyzer.analyze(program);

            // Check for semantic errors
            if (!errors.isEmpty()) {
                err.println("Semantic errors found:");
                for (SemanticError error : errors) {
                    err.println(error);
                }
                System.exit(1);
            }

            if (debug) {
                err.println("Semantic analysis completed successfully!");
            }

            // Create output directory if it doesn't exist
            File outputDir = new File(outputPath);
            if (!outputDir.exists()) {
                outputDir.mkdirs();
            }

            // Generate Jasmin assembly code
            JasminCodeGenerator codeGen = new JasminCodeGenerator(analyzer.getSymbolTable(), debug);
            String jasminCode = codeGen.generate(program);

            if (debug) {
                err.println("\nGenerated Jasmin code:");
                err.println("----------------------------");
                err.println(jasminCode);
                err.println("----------------------------\n");
            }

            // Write Main.j assembly to file
            String mainJasminFile = outputPath + "/Main.j";
            try (FileWriter writer = new FileWriter(mainJasminFile)) {
                writer.write(jasminCode);
            }

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
            if (debug) {
                err.println("\nRunning the compiled program:");
                err.println("----------------------------");
            }
            
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", outputPath, "Main");
            pb.inheritIO();
            Process process = pb.start();
            int exitCode = process.waitFor();
            
            if (debug) {
                err.println("----------------------------");
                err.println("Program finished with exit code: " + exitCode);
            }

        } catch (Exception e) {
            e.printStackTrace(err);
            System.exit(1);
        }
    }

    /**
     * Compiles a Jasmin assembly file into JVM bytecode.
     * Uses the Jasmin assembler to convert the .j file into a .class file.
     *
     * @param jasminFile Path to the input Jasmin assembly file (.j)
     * @param outputDir Directory where the compiled .class file should be placed
     */
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
            
            if (exitCode == 0 && debug) {
                err.println("Successfully compiled " + jasminFile);
            } else if (exitCode != 0) {
                err.println("Error compiling " + jasminFile + ". Exit code: " + exitCode);
                
                // Print error output if any
                try (BufferedReader reader = new BufferedReader(
                        new InputStreamReader(process.getErrorStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        err.println(line);
                    }
                }
            }
        } catch (Exception e) {
            err.println("Error compiling " + jasminFile + ": " + e.getMessage());
            e.printStackTrace(err);
        }
    }

    /**
     * Sets the output stream for normal program output.
     * Primarily used for testing purposes to redirect output.
     *
     * @param newOut The new PrintStream to use for standard output
     */
    public static void setOutputStream(PrintStream newOut) {
        out = newOut;
    }

    /**
     * Sets the error stream for error messages and debug output.
     * Primarily used for testing purposes to redirect error output.
     *
     * @param newErr The new PrintStream to use for error output
     */
    public static void setErrorStream(PrintStream newErr) {
        err = newErr;
    }
}
