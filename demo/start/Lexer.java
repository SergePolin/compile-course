import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    // Define token types for the language
    public enum TokenType {
        // Keywords
        VAR("var"),
        ROUTINE("routine"),
        IS("is"),
        INTEGER("integer"),
        REAL("real"),
        BOOLEAN("boolean"),
        STRING("string"),
        TRUE("true"),
        FALSE("false"),
        IF("if"),
        ELSE("else"),
        FOR("for"),
        REVERSE("reverse"),
        WHILE("while"),
        PRINT("print"),
        READ("read"),
        RETURN("return"),
        TYPE("type"),
        RECORD("record"),
        AND("and"),
        OR("or"),
        NOT("not"),
        XOR("xor"),

        // Operators and symbols
        ASSIGN(":="),
        EQUAL("="),
        NOT_EQUAL("/="),
        LESS("<"),
        LESS_EQUAL("<="),
        GREATER(">"),
        GREATER_EQUAL(">="),
        PLUS("\\+"),
        MINUS("-"),
        MULTIPLY("\\*"),
        DIVIDE("/"),
        MODULO("%"),
        COLON(":"),
        SEMICOLON(";"),
        COMMA(","),
        DOT("\\."),
        LPAREN("\\("),
        RPAREN("\\)"),
        LBRACKET("\\["),
        RBRACKET("\\]"),
        RANGE("\\.\\."),

        // Literals
        INTEGER_LITERAL("[0-9]+"),
        REAL_LITERAL("[0-9]+\\.[0-9]+"),
        STRING_LITERAL("\"[^\"]*\""),

        // Identifiers
        IDENTIFIER("[a-zA-Z_][a-zA-Z_0-9]*"),

        // Comments
        SINGLE_LINE_COMMENT("//[^\n]*"),
        MULTI_LINE_COMMENT("/\\*[^*]*\\*+(?:[^/*][^*]*\\*+)*/"),

        // End of input
        EOF("EOF");

        public final String pattern;

        TokenType(String pattern) {
            this.pattern = pattern;
        }
    }

    // Token class to store the type and value of a token
    public static class Token {
        public TokenType type;
        public String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("Token[type=%s, value=%s]", type.name(), value);
        }
    }

    // Lexer logic
    private String input;
    private List<Token> tokens;
    private int line = 1;

    public Lexer(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
    }

    // Tokenize the input
    public List<Token> tokenize() {
        while (!input.isEmpty()) {
            input = input.trim(); // Remove leading whitespaces
            boolean match = false;

            // Handle newlines for error reporting
            if (input.startsWith("\n")) {
                input = input.substring(1);
                line++;
                continue;
            }

            // Match each token type
            for (TokenType tokenType : TokenType.values()) {
                Pattern pattern = Pattern.compile("^" + tokenType.pattern);
                Matcher matcher = pattern.matcher(input);

                if (matcher.find()) {
                    String matchedText = matcher.group();

                    // Skip comments and don't add them as tokens
                    if (tokenType == TokenType.SINGLE_LINE_COMMENT || tokenType == TokenType.MULTI_LINE_COMMENT) {
                        input = input.substring(matchedText.length());
                        match = true;
                        break;
                    }

                    tokens.add(new Token(tokenType, matchedText));
                    input = input.substring(matchedText.length());
                    match = true;
                    break;
                }
            }

            // Handle unexpected characters
            if (!match) {
                throw new RuntimeException("Unexpected character: '" + input.charAt(0) + "' at line " + line);
            }
        }

        // Append EOF at the end of input
        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    public static void main(String[] args) {
        // Example source code that uses all features of the language
        String sourceCode = """
                // Person record definition
                type Person is record
                    var name: string;
                    var age: integer;
                end;

                /* Initialize person */
                var john: Person;

                routine main() is
                    john.name := "John";  // Assign a name
                    john.age := 30;       /* Assign age */
                    print(john.name);
                    print(john.age);
                end;

                // Factorial calculation
                routine factorial(n: integer): integer is
                    if n = 0 then
                        return 1;
                    else
                        return n * factorial(n - 1);
                    end;
                end;
                """;

        Lexer lexer = new Lexer(sourceCode);
        List<Token> tokens = lexer.tokenize();

        // Print tokens
        for (Token token : tokens) {
            System.out.println(token);
        }
    }
}
