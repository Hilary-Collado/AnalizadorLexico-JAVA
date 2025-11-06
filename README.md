
# Analizador HC — Léxico, Sintáctico y Semántico (Java + JFlex + CUP + Swing)                                                                                 
Este proyecto implementa un **analizador léxico, sintáctico y semántico completo** desarrollado en **Java** utilizando **JFlex** para la generación del analizador léxico, **JavaCUP** para la generación del analizador sintáctico, y **Swing** para la interfaz de usuario.  


# ¿Qué hace?
El sistema analiza código fuente escrito en el lenguaje educativo **MiniLang**, verificando:
1. **Análisis léxico**  
   - Divide el texto fuente en **tokens** (palabras clave, identificadores, operadores, etc.)  
   - Implementado con **JFlex** mediante el archivo `Lexer.flex`.  
   - Cada token incluye tipo, lexema, línea y columna.  

2. **Análisis sintáctico**  
   - Comprueba que la **estructura del programa** cumpla las reglas del lenguaje.  
   - Implementado con **Java CUP**, a partir del archivo `grammar.cup`.  
   - Detecta errores de sintaxis como `Se esperaba ASSIGN y llegó IDENTIFIER('mundo')`.  

3. **Análisis semántico**  
   - Valida **declaraciones y tipos de datos** (por ejemplo, si una variable fue declarada antes de usarse).  
   - Evalúa la **consistencia de operaciones** aritméticas y lógicas.  

# Interfaz gráfica
Incluye una aplicación visual con: 
- Área de texto para el código fuente.
- Tabla de tokens (con colores tipo “zebra”).
- Panel de errores léxicos o sintácticos.
- Botones:
  - **Abrir archivo**
  - **Analizar**
  - **Exportar tokens (CSV)**
  - **Cargar demo**
  - **Limpiar**

# Tecnologías utilizadas
| Componente | Tecnología / Herramienta |
|-------------|---------------------------|
| Lenguaje principal | **Java 24 (OpenJDK)** |
| Generador léxico | **JFlex 1.9.1** |
| Interfaz gráfica | **Swing** |
| Control de versiones | **Git + GitHub** |
| IDE de desarrollo | **IntelliJ IDEA** |


# Ejemplo de uso
```c
int x = 10;
float y = 2.5;
string msg = "Hola mundo!";
bool ok = true;
```

```c
int x = 10;
float y = 2.5;
string msg = "Hola mundo!";
bool ok = true;

if (x >= 10 && ok) {
   y = y + 1.0;
} else {
   y = y - 0.5;
}

return y;
```

# Ejecutable del Proyecto
El archivo ejecutable del analizador léxico se encuentra en la carpeta [`/dist`](./dist/AnalizadorLexico_HC.jar).

Desde IntelliJ IDEA:
Ejecutar la clase LexerRunner.java.

Desde terminal:
Asegúrate de que la carpeta tools/ esté junto al .jar.
Ejecuta el siguiente comando:
```bash
java -jar AnalizadorHC.jar
```

### Cómo ejecutarlo:
1. Descargue el archivo `.jar`.
2. Ejecútelo con el siguiente comando:
   ```bash
   java -jar AnalizadorLexico_HC.jar


# Hilary Collado
