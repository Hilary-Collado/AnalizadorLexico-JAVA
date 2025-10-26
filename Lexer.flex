/* JFlex specification for MiniLang */
%%
/* Directives */
%public
%class Lexer
%unicode
%line
%column
%type Token

%{
package com.hilary.lexer;

import java.util.HashMap;
import java.util.Map;

private Map<String, TokenType> keywords = new HashMap<>();
{
  keywords.put("int", TokenType.KEYWORD);
  keywords.put("float", TokenType.KEYWORD);
  keywords.put("string", TokenType.KEYWORD);
  keywords.put("bool", TokenType.KEYWORD);
  keywords.put("if", TokenType.KEYWORD);
  keywords.put("else", TokenType.KEYWORD);
  keywords.put("while", TokenType.KEYWORD);
  keywords.put("for", TokenType.KEYWORD);
  keywords.put("return", TokenType.KEYWORD);
  keywords.put("true", TokenType.KEYWORD);
  keywords.put("false", TokenType.KEYWORD);
}

private Token token(TokenType type) {
  return new Token(type, yytext(), yyline + 1, yycolumn + 1);
}
%}

/* Macros */
WHITESPACE = [ \t\f\r\n]+
ID         = [A-Za-z_][A-Za-z_0-9]*
INT        = 0|[1-9][0-9]*
FLOAT      = [0-9]+"."[0-9]+
STRING_CH  = [^\"\\n\\r\\t\\\\] | "\\\"" | "\\\\" | "\\n" | "\\t" | "\\r"
STRING     = "\""({STRING_CH})*"\""

%%

/* Ignorar espacios */
{WHITESPACE}        { /* ignore, but line/column counted */ }

/* Comentarios */
"//".*              { /* ignore line comment */ }
"/*"[^*]*"*"+([^/*][^*]*"*"+)*"/"   { /* ignore block comment */ }

/* Palabras clave e identificadores */
{ID} {
   TokenType t = keywords.getOrDefault(yytext(), TokenType.IDENTIFIER);
   return new Token(t, yytext(), yyline + 1, yycolumn + 1);
}

/* Números */
{FLOAT}             { return token(TokenType.FLOAT_LITERAL); }
{INT}               { return token(TokenType.INT_LITERAL);  }

/* Cadenas */
{STRING}            { return token(TokenType.STRING_LITERAL); }

/* Operadores dobles primero (maximal munch) */
"=="                { return token(TokenType.EQ);  }
"!="                { return token(TokenType.NE);  }
"<="                { return token(TokenType.LE);  }
">="                { return token(TokenType.GE);  }
"&&"                { return token(TokenType.AND); }
"||"                { return token(TokenType.OR);  }

/* Operadores simples */
"="                 { return token(TokenType.ASSIGN); }
"<"                 { return token(TokenType.LT);     }
">"                 { return token(TokenType.GT);     }
"+"                 { return token(TokenType.PLUS);   }
"-"                 { return token(TokenType.MINUS);  }
"*"                 { return token(TokenType.MUL);    }
"/"                 { return token(TokenType.DIV);    }
"%"                 { return token(TokenType.MOD);    }
"!"                 { return token(TokenType.NOT);    }

/* Delimitadores */
","                 { return token(TokenType.COMMA);  }
";"                 { return token(TokenType.SEMI);   }
"("                 { return token(TokenType.LPAREN); }
")"                 { return token(TokenType.RPAREN); }
"{"                 { return token(TokenType.LBRACE); }
"}"                 { return token(TokenType.RBRACE); }
"["                 { return token(TokenType.LBRACKET); }
"]"                 { return token(TokenType.RBRACKET); }

/* Caracter inesperado → ERROR */
.                   { return token(TokenType.ERROR); }

<<EOF>>             { return null; }
