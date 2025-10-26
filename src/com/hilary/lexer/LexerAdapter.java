package com.hilary.lexer;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

public class LexerAdapter {

    public static class Result {
        public final List<Token> tokens;
        public final List<String> errors;
        public Result(List<Token> tokens, List<String> errors) {
            this.tokens = tokens;
            this.errors = errors;
        }
    }

    public Result run(Reader reader) throws Exception {
        Lexer lexer = new Lexer(reader); // Lexer lo generaremos con JFlex
        List<Token> out = new ArrayList<>();
        List<String> errs = new ArrayList<>();
        Token t;
        while ((t = lexer.yylex()) != null) {
            if (t.type == TokenType.ERROR) {
                errs.add(String.format("Carácter inesperado '%s' en %d:%d", t.lexeme, t.line, t.column));
            } else {
                out.add(t);
            }
        }
        return new Result(out, errs);
    }
}