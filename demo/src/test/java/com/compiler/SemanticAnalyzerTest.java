package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java.io.StringReader;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import static org.junit.Assert.*;

@RunWith(JUnit4.class)
public class SemanticAnalyzerTest {

    private List<SemanticError> analyze(String input) throws Exception {
        ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
        Lexer lexer = new Lexer(new StringReader(input), symbolFactory);
        ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);

        Program program = (Program) parser.parse().value;
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        return analyzer.analyze(program);
    }

    @Test
    public void testBasicArithmetic() throws Exception {
        String input = 
            "routine main() is\n" +
            "    var x: integer is 5;\n" +
            "    var y: integer is 3;\n" +
            "    var sum: integer is x + y;\n" +
            "    var diff: integer is x - y;\n" +
            "    var prod: integer is x * y;\n" +
            "    var quot: integer is x / y;\n" +
            "end;";

        List<SemanticError> errors = analyze(input);
        assertTrue("Basic arithmetic operations should not produce semantic errors", errors.isEmpty());
    }

    @Test
    public void testPrintStatement() throws Exception {
        String input = 
            "routine main() is\n" +
            "    var x: integer is 42;\n" +
            "    print(x);\n" +
            "end;";

        List<SemanticError> errors = analyze(input);
        assertTrue("Print statement should not produce semantic errors", errors.isEmpty());
    }

    @Test
    public void testTypeError() throws Exception {
        String input = 
            "routine main() is\n" +
            "    var x: integer;\n" +
            "    x := \"hello\";\n" +
            "end;";

        List<SemanticError> errors = analyze(input);
        assertFalse("Should detect type mismatch error", errors.isEmpty());
        assertTrue("Error message should mention type mismatch", 
                  errors.get(0).toString().toLowerCase().contains("type"));
    }

    @Test
    public void testUndefinedVariable() throws Exception {
        String input = 
            "routine main() is\n" +
            "    print(undefinedVar);\n" +
            "end;";

        List<SemanticError> errors = analyze(input);
        assertFalse("Should detect undefined variable", errors.isEmpty());
        assertTrue("Error message should mention undefined variable",
                  errors.get(0).toString().toLowerCase().contains("undefined"));
    }

    @Test
    public void testValidAssignment() throws Exception {
        String input = 
            "routine main() is\n" +
            "    var x: integer;\n" +
            "    x := 42;\n" +
            "end;";

        List<SemanticError> errors = analyze(input);
        assertTrue("Valid assignment should not produce semantic errors", errors.isEmpty());
    }

    @Test
    public void testValidTypeAssignments() throws Exception {
        String input = 
            "routine main() is\n" +
            "    var i: integer is 42;\n" +
            "    var s: string is \"hello\";\n" +
            "    var b: boolean is true;\n" +
            "end;";

        List<SemanticError> errors = analyze(input);
        assertTrue("Valid type assignments should not produce errors", errors.isEmpty());
    }
} 