package com.compiler;

import java_cup.runtime.Scanner;
import java_cup.runtime.Symbol;
import java_cup.runtime.ComplexSymbolFactory;
import java_cup.runtime.ComplexSymbolFactory.Location;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class Lexer implements Scanner {

    private BufferedReader reader;
    private int currentChar;
    private int currentLine = 1;
    private int currentColumn = 0;
    private ComplexSymbolFactory symbolFactory;

    public Lexer(FileReader fileReader, ComplexSymbolFactory sf) {
        this.reader = new BufferedReader(fileReader);
        this.symbolFactory = sf;
        advance();
    }

    private void advance() {
        try {
            currentChar = reader.read();
            currentColumn++;
            if (currentChar == '\n') {
                currentLine++;
                currentColumn = 0;
            }
        } catch (IOException e) {
            currentChar = -1; // EOF
        }
    }

    private void skipWhiteSpace() {
        while (Character.isWhitespace(currentChar)) {
            if (currentChar == '\n') {
                currentLine++;
                currentColumn = 0;
            }
            advance();
        }
    }

    private String matchIdentifier() {
        StringBuilder builder = new StringBuilder();
        while (Character.isLetterOrDigit(currentChar) || currentChar == '_') {
            builder.append((char) currentChar);
            advance();
        }
        return builder.toString();
    }

    private String matchNumber() {
        StringBuilder builder = new StringBuilder();
        boolean isReal = false;
        while (Character.isDigit(currentChar) || currentChar == '.') {
            if (currentChar == '.') {
                if (isReal)
                    break; // Second decimal point, stop parsing
                isReal = true;
            }
            builder.append((char) currentChar);
            advance();
        }
        return builder.toString();
    }

    @Override
    public Symbol next_token() throws Exception {
        skipWhiteSpace();
        Location left = new Location(currentLine, currentColumn);

        if (currentChar == -1) {
            return symbolFactory.newSymbol("EOF", sym.EOF, left, left);
        }

        Symbol s = null;

        if (Character.isLetter(currentChar)) {
            String identifier = matchIdentifier();
            Location right = new Location(currentLine, currentColumn);

            switch (identifier) {

                case "array":
                    s = symbolFactory.newSymbol("ARRAY", sym.ARRAY, left, right);
                    break;
                case "as":
                    s = symbolFactory.newSymbol("AS", sym.AS, left, right);
                    break;
                case "print":
                    s = symbolFactory.newSymbol("PRINT", sym.PRINT, left, right);
                    break;
                case "var":
                    s = symbolFactory.newSymbol("VAR", sym.VAR, left, right);
                    break;
                case "if":
                    s = symbolFactory.newSymbol("IF", sym.IF, left, right);
                    break;
                case "else":
                    s = symbolFactory.newSymbol("ELSE", sym.ELSE, left, right);
                    break;
                case "while":
                    s = symbolFactory.newSymbol("WHILE", sym.WHILE, left, right);
                    break;
                case "for":
                    s = symbolFactory.newSymbol("FOR", sym.FOR, left, right);
                    break;
                case "routine":
                    s = symbolFactory.newSymbol("ROUTINE", sym.ROUTINE, left, right);
                    break;
                case "reverse":
                    s = symbolFactory.newSymbol("REVERSE", sym.REVERSE, left, right);
                    break;
                case "type":
                    s = symbolFactory.newSymbol("TYPE", sym.TYPE, left, right);
                    break;
                case "return":
                    s = symbolFactory.newSymbol("RETURN", sym.RETURN, left, right);
                    break;
                case "integer":
                    s = symbolFactory.newSymbol("INTEGER", sym.INTEGER, left, right);
                    break;
                case "real":
                    s = symbolFactory.newSymbol("REAL", sym.REAL, left, right);
                    break;
                case "boolean":
                    s = symbolFactory.newSymbol("BOOLEAN", sym.BOOLEAN, left, right);
                    break;
                case "string":
                    s = symbolFactory.newSymbol("STRING", sym.STRING, left, right);
                    break;
                case "true":
                    s = symbolFactory.newSymbol("TRUE", sym.TRUE, left, right, true);
                    break;
                case "false":
                    s = symbolFactory.newSymbol("FALSE", sym.FALSE, left, right, false);
                    break;
                case "is":
                    s = symbolFactory.newSymbol("IS", sym.IS, left, right);
                    break;
                case "then":
                    s = symbolFactory.newSymbol("THEN", sym.THEN, left, right);
                    break;
                case "loop":
                    s = symbolFactory.newSymbol("LOOP", sym.LOOP, left, right);
                    break;
                case "end":
                    s = symbolFactory.newSymbol("END", sym.END, left, right);
                    break;
                case "in":
                    s = symbolFactory.newSymbol("IN", sym.IN, left, right);
                    break;
                case "and":
                    s = symbolFactory.newSymbol("AND", sym.AND, left, right);
                    break;
                case "or":
                    s = symbolFactory.newSymbol("OR", sym.OR, left, right);
                    break;
                case "not":
                    s = symbolFactory.newSymbol("NOT", sym.NOT, left, right);
                    break;
                case "xor":
                    s = symbolFactory.newSymbol("XOR", sym.XOR, left, right);
                    break;
                default:
                    s = symbolFactory.newSymbol("IDENTIFIER", sym.IDENTIFIER, left, right, identifier);
                    break;
            }
        } else if (currentChar == '-') {
            advance();
            if (Character.isDigit(currentChar)) {
                String number = matchNumber();
                Location right = new Location(currentLine, currentColumn);
                if (number.contains(".")) {
                    s = symbolFactory.newSymbol("REAL_LITERAL", sym.REAL_LITERAL,
                            left, right, -Double.parseDouble(number));
                } else {
                    s = symbolFactory.newSymbol("INTEGER_LITERAL", sym.INTEGER_LITERAL,
                            left, right, -Integer.parseInt(number));
                }
            } else {
                Location right = new Location(currentLine, currentColumn);
                s = symbolFactory.newSymbol("MINUS", sym.MINUS, left, right);
            }
        } else if (Character.isDigit(currentChar)) {
            String number = matchNumber();
            Location right = new Location(currentLine, currentColumn);
            if (number.contains(".")) {
                s = symbolFactory.newSymbol("REAL_LITERAL", sym.REAL_LITERAL, left, right, Double.parseDouble(number));
            } else {
                s = symbolFactory.newSymbol("INTEGER_LITERAL", sym.INTEGER_LITERAL, left, right,
                        Integer.parseInt(number));
            }
        } else if (currentChar == '.') {
            advance();
            if (currentChar == '.') { // Check for second dot (..)
                advance();
                Location right = new Location(currentLine, currentColumn);
                s = symbolFactory.newSymbol("RANGE", sym.RANGE, left, right); // Return RANGE token
            } else {
                Location right = new Location(currentLine, currentColumn);
                s = symbolFactory.newSymbol("DOT", sym.DOT, left, right); // Return single DOT token
            }
        } else if (currentChar == '"') {
            advance();
            StringBuilder stringLiteral = new StringBuilder();
            while (currentChar != '"' && currentChar != -1) {
                stringLiteral.append((char) currentChar);
                advance();
            }
            advance(); // Skip closing "
            Location right = new Location(currentLine, currentColumn);
            s = symbolFactory.newSymbol("STRING_LITERAL", sym.STRING_LITERAL, left, right, stringLiteral.toString());
        } else {
            Location right = new Location(currentLine, currentColumn + 1);
            switch (currentChar) {
                case ':':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        s = symbolFactory.newSymbol("ASSIGN", sym.ASSIGN, left, right);
                    } else {
                        s = symbolFactory.newSymbol("COLON", sym.COLON, left, right);
                    }
                    break;
                case ';':
                    advance();
                    s = symbolFactory.newSymbol("SEMICOLON", sym.SEMICOLON, left, right);
                    break;
                case '(':
                    advance();
                    s = symbolFactory.newSymbol("LPAREN", sym.LPAREN, left, right);
                    break;
                case ')':
                    advance();
                    s = symbolFactory.newSymbol("RPAREN", sym.RPAREN, left, right);
                    break;
                case '[':
                    advance();
                    s = symbolFactory.newSymbol("LBRACKET", sym.LBRACKET, left, right);
                    break;
                case ']':
                    advance();
                    s = symbolFactory.newSymbol("RBRACKET", sym.RBRACKET, left, right);
                    break;
                case '+':
                    advance();
                    s = symbolFactory.newSymbol("PLUS", sym.PLUS, left, right);
                    break;
                case '-':
                    advance();
                    s = symbolFactory.newSymbol("MINUS", sym.MINUS, left, right);
                    break;
                case '*':
                    advance();
                    s = symbolFactory.newSymbol("MULTIPLY", sym.MULTIPLY, left, right);
                    break;
                case '/':
                    advance();
                    s = symbolFactory.newSymbol("DIVIDE", sym.DIVIDE, left, right);
                    break;
                case '>':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        s = symbolFactory.newSymbol("GREATER_OR_EQUAL", sym.GREATER_OR_EQUAL, left, right);
                    } else {
                        s = symbolFactory.newSymbol("GREATER", sym.GREATER, left, right);
                    }
                    break;
                case '<':
                    advance();
                    if (currentChar == '=') {
                        advance();
                        s = symbolFactory.newSymbol("LESS_OR_EQUAL", sym.LESS_OR_EQUAL, left, right);
                    } else {
                        s = symbolFactory.newSymbol("LESS", sym.LESS, left, right);
                    }
                    break;
                case '=':
                    advance();
                    s = symbolFactory.newSymbol("EQUAL", sym.EQUAL, left, right);
                    break;
                default:
                    System.err.println("Unknown character: " + (char) currentChar);
                    advance();
                    return next_token(); // Skip unknown characters and continue
            }
        }

        debugToken(s);
        return s;
    }

    private void debugToken(Symbol s) {
        System.out.println("DEBUG: Token - " + sym.terminalNames[s.sym] +
                " (Line: " + ((ComplexSymbolFactory.ComplexSymbol) s).xleft.getLine() +
                ", Column: " + ((ComplexSymbolFactory.ComplexSymbol) s).xleft.getColumn() +
                ", Value: " + s.value + ")");
    }
}