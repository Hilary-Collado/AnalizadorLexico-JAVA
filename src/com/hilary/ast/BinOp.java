package com.hilary.ast;

public class BinOp implements  Expr{
    public final String op; public final Expr a,b;
    public BinOp(String op, Expr a, Expr b){ this.op=op; this.a=a; this.b=b; }
}
