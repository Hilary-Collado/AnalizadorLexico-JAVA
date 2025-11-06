package com.hilary.run;

import java.io.StringReader;
import com.hilary.parser.Parser;
import com.hilary.ast.Program;
import com.hilary.sema.SemanticAnalyzer;

public class ParseRunner {
    public static void main(String[] args) throws Exception {
        String source = """
      int x;
      float y;
      x = 1 + 2;
      y = x + 3.5;
      { int x; x = 5; }
    """;

        Parser parser = new Parser(new StringReader(source));
        Program prog = parser.parse();
        new SemanticAnalyzer().check(prog);
        System.out.println("Parse + Semántica OK ✅");
    }
}
