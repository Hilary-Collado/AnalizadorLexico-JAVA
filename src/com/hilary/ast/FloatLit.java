package com.hilary.ast;

public class FloatLit implements Expr{
    public final double v;
    public FloatLit(double v){ this.v=v; }
}