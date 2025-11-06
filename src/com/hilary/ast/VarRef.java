package com.hilary.ast;

public class VarRef implements Expr{
    public final String name;
    public VarRef(String n){ this.name=n; }
}
