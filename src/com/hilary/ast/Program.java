package com.hilary.ast;

import java.util.List;

public class Program {
    public final List<Stmt> stmts;
    public Program(List<Stmt> stmts){ this.stmts = stmts; }
}
