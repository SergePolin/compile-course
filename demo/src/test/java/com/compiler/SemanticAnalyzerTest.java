package com.compiler;

import com.compiler.semantic.SemanticAnalyzer;
import com.compiler.semantic.SemanticError;
import java_cup.runtime.ComplexSymbolFactory;
import java.io.StringReader;
import java.util.List;
import org.junit.Test;
import static org.junit.Assert.*;

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
} 