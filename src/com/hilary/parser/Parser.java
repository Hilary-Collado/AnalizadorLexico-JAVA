package com.hilary.parser;

import com.hilary.lexer.Lexer;
import com.hilary.lexer.Token;
import com.hilary.lexer.TokenType;
import com.hilary.ast.*;

import java.io.Reader;
import java.util.ArrayList;
import java.util.List;


public class Parser {
    private final Lexer lexer;
    private Token look; // token actual (lookahead)

    public Parser(Reader reader) throws Exception {
        this.lexer = new Lexer(reader);
        this.look = next(); // primer token
    }

    /** API principal: parsear y devolver un AST Program */
    public Program parse() throws Exception {
        List<Stmt> stmts = new ArrayList<>();
        while (look != null) {
            stmts.add(parseStmt());
        }
        return new Program(stmts);
    }

    /* =========== utilidades =========== */

    private Token next() throws Exception { return lexer.yylex(); }

    private boolean is(TokenType t){ return look != null && look.type == t; }
    private boolean isKeyword(String kw){
        return look != null && look.type == TokenType.KEYWORD && kw.equals(look.lexeme);
    }

    private Token expect(TokenType t) throws Exception {
        if (!is(t)) error("Se esperaba " + t + " y llegó " + repr(look));
        Token cur = look; look = next(); return cur;
    }
    private Token expectKeyword(String kw) throws Exception {
        if (!isKeyword(kw)) error("Se esperaba palabra clave '"+kw+"' y llegó " + repr(look));
        Token cur = look; look = next(); return cur;
    }

    private static String repr(Token t){
        return (t==null) ? "EOF" : (t.type + "('" + t.lexeme + "') @" + t.line + ":" + t.column);
    }

    private void error(String msg) {
        throw new RuntimeException("Sintáctico: " + msg);
    }

    /* =========== reglas =========== */

    private Stmt parseStmt() throws Exception {
        if (is(TokenType.KEYWORD) && isTypeKeyword(look.lexeme)) {
            String ty = parseType();
            String name = expect(TokenType.IDENTIFIER).lexeme;
            expect(TokenType.SEMI);
            return new VarDecl(ty, name);
        }
        if (is(TokenType.IDENTIFIER)) {
            String name = look.lexeme;
            look = next();
            expect(TokenType.ASSIGN);
            Expr e = parseExpr();
            expect(TokenType.SEMI);
            return new Assign(name, e);
        }
        if (is(TokenType.LBRACE)) {
            look = next(); // consume '{'
            List<Stmt> inner = new ArrayList<>();
            while (look != null && !is(TokenType.RBRACE)) {
                inner.add(parseStmt());
            }
            expect(TokenType.RBRACE);
            return new Block(inner);
        }
        error("Inicio de sentencia no válido: " + repr(look));
        return null; // unreachable
    }

    private boolean isTypeKeyword(String s){
        return "int".equals(s) || "float".equals(s) || "bool".equals(s) || "string".equals(s);
    }

    private String parseType() throws Exception {
        if (isKeyword("int"))    { expectKeyword("int");    return "int"; }
        if (isKeyword("float"))  { expectKeyword("float");  return "float"; }
        if (isKeyword("bool"))   { expectKeyword("bool");   return "bool"; }
        if (isKeyword("string")) { expectKeyword("string"); return "string"; }
        error("Tipo no válido: " + repr(look));
        return "?";
    }

    private Expr parseExpr() throws Exception {
        Expr e = parseTerm();
        while (look != null && (is(TokenType.PLUS) || is(TokenType.MINUS))) {
            String op = look.lexeme;
            look = next();
            Expr b = parseTerm();
            e = new BinOp(op, e, b);
        }
        return e;
    }

    private Expr parseTerm() throws Exception {
        Expr e = parseFactor();
        while (look != null && (is(TokenType.MUL) || is(TokenType.DIV))) {
            String op = look.lexeme;
            look = next();
            Expr b = parseFactor();
            e = new BinOp(op, e, b);
        }
        return e;
    }

    private Expr parseFactor() throws Exception {
        if (is(TokenType.INT_LITERAL))   { int v = Integer.parseInt(look.lexeme); look = next(); return new IntLit(v); }
        if (is(TokenType.FLOAT_LITERAL)) { double v = Double.parseDouble(look.lexeme); look = next(); return new FloatLit(v); }
        if (is(TokenType.STRING_LITERAL)){ String v = look.lexeme; look = next(); return new StringLit(v); }
        if (isKeyword("true"))  { look = next(); return new BoolLit(true); }
        if (isKeyword("false")) { look = next(); return new BoolLit(false); }
        if (is(TokenType.IDENTIFIER))    { String n = look.lexeme; look = next(); return new VarRef(n); }
        if (is(TokenType.LPAREN)) {
            look = next();
            Expr e = parseExpr();
            expect(TokenType.RPAREN);
            return e;
        }
        error("Expresión no válida: " + repr(look));
        return null;
    }
}
