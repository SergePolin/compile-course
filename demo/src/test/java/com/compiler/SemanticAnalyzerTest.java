package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java.io.StringReader;
import java.util.List;
import org.junit.Test;
import org.junit.Assert;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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
    public void testUndefinedVariable() throws Exception {
        String input = "routine main() is\n    print(a);\nend;";
        List<SemanticError> errors = analyze(input);

        assertFalse("Should have detected undefined variable", errors.isEmpty());
        assertTrue("Error message should mention undefined variable",
                errors.get(0).toString().contains("Undefined variable"));
    }

    @Test
    public void testUnusedVariableRemoval() throws Exception {
        String input = 
            "routine main() : void is\n" +
            "    var a: integer is 5;  // Used variable\n" +
            "    var b: integer is 10; // Unused variable\n" +
            "    print(a);\n" +
            "end;";

        ComplexSymbolFactory symbolFactory = new ComplexSymbolFactory();
        Lexer lexer = new Lexer(new StringReader(input), symbolFactory);
        ImperativeLangParser parser = new ImperativeLangParser(lexer, symbolFactory);
        
        Program program = (Program) parser.parse().value;
        SemanticAnalyzer analyzer = new SemanticAnalyzer();
        analyzer.analyze(program);

        // Check that only variable 'a' remains in the AST
        boolean foundA = false;
        boolean foundB = false;
        for (Statement stmt : program.getStatements()) {
            if (stmt instanceof VarDecl) {
                VarDecl varDecl = (VarDecl) stmt;
                if (varDecl.getName().equals("a")) foundA = true;
                if (varDecl.getName().equals("b")) foundB = true;
            }
        }

        assertTrue("Used variable 'a' should be present", foundA);
        assertFalse("Unused variable 'b' should be removed", foundB);
    }
} 