package com.hilary.ast;

import java.util.List;

public class Block implements  Stmt{
    public final List<Stmt> stmts;
    public Block(List<Stmt> stmts){this.stmts = stmts;}
}
