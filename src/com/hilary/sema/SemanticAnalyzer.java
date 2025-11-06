package com.hilary.sema;
import com.hilary.ast.*;

public class SemanticAnalyzer {
    private final SymbolTable st = new SymbolTable();

    public void check(Program p){
        for (Stmt s : p.stmts) checkStmt(s);
    }

    private void checkStmt(Stmt s){
        if (s instanceof VarDecl vd) {
            if (!st.declare(vd.name, vd.type))
                error("Variable redeclarada: " + vd.name);
        } else if (s instanceof Assign a) {
            String tVar = st.lookup(a.name);
            if (tVar == null) error("Variable no declarada: " + a.name);
            String tExpr = typeOf(a.expr);
            if (tVar != null && !compatible(tVar, tExpr))
                error("Tipos incompatibles en asignación: " + tVar + " = " + tExpr);
        } else if (s instanceof Block b) {
            st.push();
            for (Stmt x : b.stmts) checkStmt(x);
            st.pop();
        }
    }

    private String typeOf(Expr e){
        if (e instanceof IntLit) return "int";
        if (e instanceof FloatLit) return "float";
        if (e instanceof StringLit) return "string";
        if (e instanceof BoolLit) return "bool";
        if (e instanceof VarRef v){
            String t = st.lookup(v.name);
            if (t == null) error("Uso de variable no declarada: " + v.name);
            return t == null ? "<?>": t;
        }
        if (e instanceof BinOp b){
            String ta = typeOf(b.a), tb = typeOf(b.b);
            if (isNum(ta) && isNum(tb)) return (ta.equals("float") || tb.equals("float")) ? "float" : "int";
            error("Operación aritmética inválida entre " + ta + " y " + tb);
            return "<?>"; // unreachable normalmente
        }
        return "<?>"; // por simplicidad
    }

    private boolean isNum(String t){ return "int".equals(t) || "float".equals(t); }
    private boolean compatible(String lhs, String rhs){
        if (lhs.equals(rhs)) return true;
        // promoción numérica: int <- float NO, float <- int SÍ
        return lhs.equals("float") && rhs.equals("int");
    }

    private void error(String msg){ throw new RuntimeException("Semántico: " + msg); }
}
