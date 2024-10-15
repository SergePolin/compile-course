package src;

import java.io.*;
import java.util.regex.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java_cup.runtime.Symbol;
import src.sym;

public class Lexer implements java_cup.runtime.Scanner {

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
        IN("in"),
        THEN("then"),
        LOOP("loop"),

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
        EOF("EOF"),
        END("end");

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
    private int currentTokenIndex;
    private int line = 1;

    public Lexer(String input) {
        this.input = input;
        this.tokens = new ArrayList<>();
        this.currentTokenIndex = 0;
    }

    public Lexer(Reader reader) {
        this.input = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
        this.tokens = new ArrayList<>();
        this.currentTokenIndex = 0;
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

    // Read the entire file content as a string
    public static String readFile(String filePath) throws IOException {
        StringBuilder contentBuilder = new StringBuilder();
        try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                contentBuilder.append(currentLine).append("\n");
            }
        }
        return contentBuilder.toString();
    }

    public static void main(String[] args) {
        if (args.length != 1) {
            System.out.println("Usage: java Lexer <file-path>");
            return;
        }

        try {
            // Read the input file
            String filePath = args[0];
            String sourceCode = readFile(filePath);

            // Create a lexer and tokenize the file content
            Lexer lexer = new Lexer(sourceCode);
            List<Token> tokens = lexer.tokenize();

            // Print tokens
            for (Token token : tokens) {
                System.out.println(token);
            }

        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        } catch (RuntimeException e) {
            System.err.println("Lexical error: " + e.getMessage());
        }
    }

    // Define constants for symbol values
    private static final int INTEGER_LITERAL_SYM = 1;
    private static final int PLUS_SYM = 2;
    private static final int MINUS_SYM = 3;
    private static final int MULTIPLY_SYM = 4;
    private static final int DIVIDE_SYM = 5;
    private static final int LPAREN_SYM = 6;
    private static final int RPAREN_SYM = 7;
    private static final int EOF_SYM = 0;
    private static final int ERROR_SYM = -1;

    @Override
    public Symbol next_token() throws Exception {
        if (tokens.isEmpty()) {
            tokenize();
        }

        if (currentTokenIndex >= tokens.size()) {
            return new Symbol(sym.EOF);
        }

        Token token = tokens.get(currentTokenIndex++);
        switch (token.type) {
            case INTEGER_LITERAL:
                return new Symbol(sym.INTEGER_LITERAL, Integer.parseInt(token.value));
            case REAL_LITERAL:
                return new Symbol(sym.REAL_LITERAL, Double.parseDouble(token.value));
            case STRING_LITERAL:
                return new Symbol(sym.STRING_LITERAL, token.value);
            case IDENTIFIER:
                return new Symbol(sym.IDENTIFIER, token.value);
            case PLUS:
                return new Symbol(sym.PLUS);
            case MINUS:
                return new Symbol(sym.MINUS);
            case MULTIPLY:
                return new Symbol(sym.TIMES);
            case DIVIDE:
                return new Symbol(sym.DIVIDE);
            case MODULO:
                return new Symbol(sym.MODULO);
            case LPAREN:
                return new Symbol(sym.LPAREN);
            case RPAREN:
                return new Symbol(sym.RPAREN);
            case LBRACKET:
                return new Symbol(sym.LBRACKET);
            case RBRACKET:
                return new Symbol(sym.RBRACKET);
            case ASSIGN:
                return new Symbol(sym.ASSIGN);
            case EQUAL:
                return new Symbol(sym.EQUAL);
            case NOT_EQUAL:
                return new Symbol(sym.NOT_EQUAL);
            case LESS:
                return new Symbol(sym.LESS);
            case LESS_EQUAL:
                return new Symbol(sym.LESS_EQUAL);
            case GREATER:
                return new Symbol(sym.GREATER);
            case GREATER_EQUAL:
                return new Symbol(sym.GREATER_EQUAL);
            case COLON:
                return new Symbol(sym.COLON);
            case SEMICOLON:
                return new Symbol(sym.SEMICOLON);
            case COMMA:
                return new Symbol(sym.COMMA);
            case DOT:
                return new Symbol(sym.DOT);
            case RANGE:
                return new Symbol(sym.RANGE);
            case VAR:
                return new Symbol(sym.VAR);
            case ROUTINE:
                return new Symbol(sym.ROUTINE);
            case IS:
                return new Symbol(sym.IS);
            case IF:
                return new Symbol(sym.IF);
            case ELSE:
                return new Symbol(sym.ELSE);
            case WHILE:
                return new Symbol(sym.WHILE);
            case FOR:
                return new Symbol(sym.FOR);
            case REVERSE:
                return new Symbol(sym.REVERSE);
            case PRINT:
                return new Symbol(sym.PRINT);
            case READ:
                return new Symbol(sym.READ);
            case RETURN:
                return new Symbol(sym.RETURN);
            case TYPE:
                return new Symbol(sym.TYPE);
            case RECORD:
                return new Symbol(sym.RECORD);
            case AND:
                return new Symbol(sym.AND);
            case OR:
                return new Symbol(sym.OR);
            case NOT:
                return new Symbol(sym.NOT);
            case XOR:
                return new Symbol(sym.XOR);
            case TRUE:
                return new Symbol(sym.TRUE);
            case FALSE:
                return new Symbol(sym.FALSE);
            case EOF:
                return new Symbol(sym.EOF);
            case IN:
                return new Symbol(sym.IN);
            case END:
                return new Symbol(sym.END);
            case STRING:
                return new Symbol(sym.STRING);
            case THEN:
                return new Symbol(sym.THEN);
            case LOOP:
                return new Symbol(sym.LOOP);
            case INTEGER:
                return new Symbol(sym.INTEGER);
            default:
                throw new RuntimeException("Unexpected token: " + token);
        }
    }

    private int getSymbolId(TokenType type) {
        switch (type) {
            case INTEGER_LITERAL:
                return INTEGER_LITERAL_SYM;
            case PLUS:
                return PLUS_SYM;
            case MINUS:
                return MINUS_SYM;
            case MULTIPLY:
                return MULTIPLY_SYM;
            case DIVIDE:
                return DIVIDE_SYM;
            case LPAREN:
                return LPAREN_SYM;
            case RPAREN:
                return RPAREN_SYM;
            default:
                return ERROR_SYM;
        }
    }
}
