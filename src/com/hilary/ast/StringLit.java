package com.hilary.ast;

public class StringLit implements Expr{
    public final String v;
    public StringLit(String v){ this.v=v; }
}
