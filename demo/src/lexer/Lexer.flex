package lexer;

import java_cup.runtime.Symbol;

%% 

%class Lexer
%unicode
%cup
%public
%{

// Java imports and fields
private java_cup.runtime.Symbol sym(int type) {
    return new java_cup.runtime.Symbol(type);
}

%}

/* Definitions */
INT = [0-9]+
ID = [a-zA-Z_][a-zA-Z0-9_]*
WHITESPACE = [ \t\r\n]+

%% 

/* Lexical rules */
<YYINITIAL> {

    "routine"          { return sym(sym.ROUTINE); }
    "var"              { return sym(sym.VAR); }
    "if"               { return sym(sym.IF); }
    "else"             { return sym(sym.ELSE); }
    "for"              { return sym(sym.FOR); }
    "while"            { return sym(sym.WHILE); }
    "return"           { return sym(sym.RETURN); }
    "integer"          { return sym(sym.INTEGER); }
    "real"             { return sym(sym.REAL); }
    "boolean"          { return sym(sym.BOOLEAN); }
    "true"             { return sym(sym.TRUE); }
    "false"            { return sym(sym.FALSE); }
    "print"            { return sym(sym.PRINT); }

    // Operators
    "+"                { return sym(sym.ADD); }
    "-"                { return sym(sym.SUB); }
    "*"                { return sym(sym.MUL); }
    "/"                { return sym(sym.DIV); }
    "%"                { return sym(sym.MOD); }
    ":="               { return sym(sym.ASSIGN); }
    "="                { return sym(sym.EQ); }
    "<="               { return sym(sym.LEQ); }
    ">="               { return sym(sym.GEQ); }
    "<"                { return sym(sym.LT); }
    ">"                { return sym(sym.GT); }

    // Punctuation
    ";"                { return sym(sym.SEMICOLON); }
    ","                { return sym(sym.COMMA); }
    "("                { return sym(sym.LPAREN); }
    ")"                { return sym(sym.RPAREN); }

    // Identifiers
    {ID}               { return new java_cup.runtime.Symbol(sym.IDENTIFIER, yytext()); }

    // Integers
    {INT}              { return new java_cup.runtime.Symbol(sym.INTEGER_LITERAL, Integer.parseInt(yytext())); }

    // Whitespace
    {WHITESPACE}       { /* Skip whitespace */ }

    // Error handling
    .                  { System.err.println("Unrecognized character: " + yytext()); }

}
