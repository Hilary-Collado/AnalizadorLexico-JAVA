package com.hilary.ast;
public class VarDecl implements Stmt {
    public final String type, name;
    public VarDecl(String type, String name){ this.type = type; this.name = name; }
}