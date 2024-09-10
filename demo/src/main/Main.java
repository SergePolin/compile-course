package main;

import parser.Parser;
import lexer.Lexer;
import java.io.FileReader;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {

        String inputFilePath = args[0];

        try {
            FileReader fileReader = new FileReader(inputFilePath);

            Lexer lexer = new Lexer(fileReader);

            Parser parser = new Parser(lexer);

            parser.parse();

            System.out.println("Parsing completed successfully.");

        } catch (IOException e) {
            System.err.println("Error reading the input file: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Error during parsing: " + e.getMessage());
        }
    }
}
