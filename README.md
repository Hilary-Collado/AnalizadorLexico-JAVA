
# Analizador Léxico (Java + JFlex + Swing)                                                                                      

Este proyecto implementa un **analizador léxico gráfico** desarrollado en **Java** utilizando **JFlex** para la generación del analizador, y **Swing** para la interfaz de usuario.  
---

# ¿Cómo funciona?

El analizador léxico es la primera fase de un compilador.  
Su función es **leer el código fuente** y **dividirlo en unidades léxicas llamadas *tokens***.  
Cada token representa un componente del lenguaje, como palabras reservadas, identificadores, números, operadores o delimitadores.

1. El archivo `Lexer.flex` define las **reglas léxicas** usando expresiones regulares.  
2. **JFlex** genera la clase `Lexer.java`, que procesa el texto según esas reglas.  
3. Cada coincidencia se convierte en un objeto `Token` con:
   - Tipo de token (`KEYWORD`, `IDENTIFIER`, `INT_LITERAL`, etc.)  
   - Lexema (texto reconocido)  
   - Línea y columna del código fuente

4. Los resultados se muestran en una interfaz visual con una tabla de tokens, lista de errores y botones de acción.

---

# Características

 **Análisis léxico completo** con detección de palabras clave, números, cadenas, operadores y delimitadores  
 **Interfaz gráfica moderna** desarrollada en Java Swing  
 **Exportación de tokens a CSV**  
 **Cargar código demo** y **botón de limpieza rápida**  
 **Tabla con diseño tipo “zebra”** (filas alternadas en color claro y oscuro)

---

# Tecnologías utilizadas

| Componente | Tecnología / Herramienta |
|-------------|---------------------------|
| Lenguaje principal | **Java 24 (OpenJDK)** |
| Generador léxico | **JFlex 1.9.1** |
| Interfaz gráfica | **Swing** |
| Control de versiones | **Git + GitHub** |
| IDE de desarrollo | **IntelliJ IDEA** |

---

# Ejemplo de uso

**Entrada (MiniLang):**
int x = 10;
float y = 2.5;
string msg = "Hola mundo!";
bool ok = true;


# Hilary Collado
