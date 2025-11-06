package com.hilary.ast;

public class Assign implements Stmt {
    public final String name;
    public final Expr expr;
    public Assign(String name, Expr expr){ this.name = name; this.expr = expr; }
}