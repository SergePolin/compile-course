import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;

public class Lexer {

    // Define token types
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

        // Identifiers (for variable names, routine names, etc.)
        IDENTIFIER("[a-zA-Z_][a-zA-Z_0-9]*"),

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

    public Lexer(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
    }

    // Tokenize the input
    public List<Token> tokenize() {
        while (!input.isEmpty()) {
            boolean match = false;
            input = input.trim(); // Remove leading whitespaces

            for (TokenType tokenType : TokenType.values()) {
                Pattern pattern = Pattern.compile("^" + tokenType.pattern);
                Matcher matcher = pattern.matcher(input);

                if (matcher.find()) {
                    String matchedText = matcher.group();
                    tokens.add(new Token(tokenType, matchedText));
                    input = input.substring(matchedText.length());
                    match = true;
                    break;
                }
            }

            if (!match) {
                throw new RuntimeException("Unexpected character: " + input.charAt(0));
            }
        }

        tokens.add(new Token(TokenType.EOF, ""));
        return tokens;
    }

    public static void main(String[] args) {
        String sourceCode = """
                type Person is record
                    var name: string;
                    var age: integer;
                end;

                var john: Person;

                routine main() is
                    john.name := "John";
                    john.age := 30;
                    print(john.name);
                    print(john.age);
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
