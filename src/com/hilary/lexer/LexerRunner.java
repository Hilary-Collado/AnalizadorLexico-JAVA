package com.hilary.lexer;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.geom.RoundRectangle2D;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.List;
import com.hilary.parser.Parser;
import com.hilary.ast.Program;
import  com.hilary.sema.SemanticAnalyzer;
import java.io.StringReader;

public class LexerRunner extends JFrame {
    // ===== Paleta =====
    private static final Color BG_APP         = new Color(0xF7F8FB);
    private static final Color BG_PANEL       = Color.WHITE;
    private static final Color BG_TOOLBAR     = new Color(0xFFFFFF);
    private static final Color FG_TEXT        = new Color(0x111827);
    private static final Color TABLE_ROW_EVEN = new Color(0xFAFAFC);
    private static final Color TABLE_ROW_ODD  = new Color(0xF0F2F7);

    // Botones (degradados tipo “Email” de tu imagen)
    private static final Color GRAD_PURPLE_A  = new Color(0x8B5CF6); // start
    private static final Color GRAD_PURPLE_B  = new Color(0x6366F1); // end
    private static final Color GRAD_GREEN_A   = new Color(0x10B981);
    private static final Color GRAD_GREEN_B   = new Color(0x059669);
    private static final Color GRAD_BLUE_A    = new Color(0x38BDF8);
    private static final Color GRAD_BLUE_B    = new Color(0x3B82F6);
    private static final Color GRAD_GRAY_A    = new Color(0x9CA3AF);
    private static final Color GRAD_GRAY_B    = new Color(0x6B7280);
    private static final Color GRAD_RED_A     = new Color(0xF97316);
    private static final Color GRAD_RED_B     = new Color(0xDC2626);
    private final JTextArea inputArea = new JTextArea();
    private final JTable tokenTable = new JTable();
    private final JTextArea errorArea = new JTextArea();

    public LexerRunner() {
//        super("MiniLang Lexer (Java + JFlex)");
        super("lexical analyzer (Java + JFlex)");

        try { UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()); } catch (Exception ignored) {}

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1000, 700);
        setLocationRelativeTo(null);

        inputArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 15));
        inputArea.setBackground(BG_PANEL);
        inputArea.setForeground(FG_TEXT);
        inputArea.setCaretColor(FG_TEXT);
        inputArea.setMargin(new Insets(10,10,10,10));

        errorArea.setEditable(false);
        errorArea.setForeground(new Color(180, 0, 0));
        errorArea.setBackground(BG_PANEL);
        errorArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 13));


        JScrollPane left = new JScrollPane(inputArea);
        left.setBorder(BorderFactory.createTitledBorder("Código fuente (MiniLang)"));

        JScrollPane rightTop = new JScrollPane(tokenTable);
        rightTop.setBorder(BorderFactory.createTitledBorder("Tokens"));

        JScrollPane rightBottom = new JScrollPane(errorArea);
        rightBottom.setBorder(BorderFactory.createTitledBorder("Errores léxicos"));

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, rightTop, rightBottom);
        rightSplit.setResizeWeight(0.6);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, left, rightSplit);
        mainSplit.setResizeWeight(0.5);

        JPanel toolbar = buildToolbar();

        getContentPane().setLayout(new BorderLayout());
        getContentPane().add(toolbar, BorderLayout.NORTH);
        getContentPane().add(mainSplit, BorderLayout.CENTER);
        getContentPane().setBackground(BG_APP);

        tokenTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"#","Tipo","Lexema","Línea","Columna"}));
        styleTable(tokenTable);

        // Fondo general
        getContentPane().setBackground(BG_APP);
    }

    // ===== Botón pill custom pintado a mano (ignora L&F) =====
    static class PillButton extends JButton {
        private Color start, end;
        private final int radius = 18;

        PillButton(String text, Color start, Color end) {
            super(text);
            this.start = start;
            this.end = end;
            setForeground(Color.WHITE);
            setOpaque(false);
            setFocusPainted(false);
            setBorderPainted(false);
            setContentAreaFilled(false);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setFont(new Font("Segoe UI", Font.BOLD, 12));
            setMargin(new Insets(8,16,8,16));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Hover/pressed
            Color a = start, b = end;
            if (getModel().isPressed()) {
                a = darken(a, 0.12f); b = darken(b, 0.12f);
            } else if (getModel().isRollover()) {
                a = brighten(a, 0.10f); b = brighten(b, 0.10f);
            }

            GradientPaint gp = new GradientPaint(0, 0, a, 0, getHeight(), b);
            g2.setPaint(gp);
            Shape rr = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), radius*2, radius*2);
            g2.fill(rr);

            // Sombra ligera del texto
            g2.setColor(new Color(0,0,0,40));
            FontMetrics fm = g2.getFontMetrics(getFont());
            int tw = fm.stringWidth(getText());
            int th = fm.getAscent();
            int x = (getWidth() - tw) / 2;
            int y = (getHeight() + th) / 2 - 2;
            g2.drawString(getText(), x, y+1);

            // Texto
            g2.setColor(getForeground());
            g2.drawString(getText(), x, y);

            g2.dispose();
        }

        private static Color darken(Color c, float f) {
            f = Math.min(1f, Math.max(0f, f));
            return new Color(
                    Math.max(0, (int)(c.getRed()   * (1f - f))),
                    Math.max(0, (int)(c.getGreen() * (1f - f))),
                    Math.max(0, (int)(c.getBlue()  * (1f - f))));
        }
        private static Color brighten(Color c, float f) {
            f = Math.min(1f, Math.max(0f, f));
            int r = c.getRed(), g = c.getGreen(), b = c.getBlue();
            int nr = r + (int)((255 - r) * f);
            int ng = g + (int)((255 - g) * f);
            int nb = b + (int)((255 - b) * f);
            return new Color(nr, ng, nb);
        }
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bar.setBackground(BG_TOOLBAR);

        JButton open     = new PillButton("Abrir…", GRAD_GRAY_A,  GRAD_GRAY_B);
        open.addActionListener(this::onOpen);

        JButton analyze  = new PillButton("Analizar", GRAD_PURPLE_A, GRAD_PURPLE_B); // como “Email”
        analyze.addActionListener(this::onAnalyze);

        JButton parseBtn = new PillButton("Parse + Semántica", GRAD_BLUE_A, GRAD_BLUE_B);
        parseBtn.addActionListener(ev -> {
            try {
                Parser parser = new Parser(new StringReader(inputArea.getText()));
                Program prog = parser.parse();
                new SemanticAnalyzer().check(prog);
                errorArea.setForeground(new Color(0x065F46));
                errorArea.setText("Sintaxis OK.\nSemántica OK.");
            } catch (Exception ex) {
                errorArea.setForeground(new Color(180, 0, 0));
                errorArea.setText(ex.getMessage());
            }
        });
        bar.add(parseBtn);

        JButton exportCsv= new PillButton("Exportar tokens (CSV)", GRAD_GREEN_A, GRAD_GREEN_B);
        exportCsv.addActionListener(this::onExportCsv);

        JButton demo     = new PillButton("Cargar demo", GRAD_BLUE_A, GRAD_BLUE_B);
        demo.addActionListener(e -> loadDemo());

        JButton clear    = new PillButton("Limpiar", GRAD_RED_A, GRAD_RED_B);
        clear.addActionListener(e -> clearAll());

        bar.add(open);
        bar.add(analyze);
        bar.add(exportCsv);
        bar.add(demo);
        bar.add(clear);
        return bar;
    }

    /** Zebra striping + ajustes visuales de tabla */
    private void styleTable(JTable table) {
        table.setFillsViewportHeight(true);
        table.setRowHeight(26);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setPreferredSize(new Dimension(0, 32));
        table.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        table.setForeground(FG_TEXT);

        DefaultTableCellRenderer z = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object value, boolean isSelected, boolean hasFocus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, value, isSelected, hasFocus, row, col);
                if (!isSelected) c.setBackground((row % 2 == 0) ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
                return c;
            }
        };
        for (int i = 0; i < table.getColumnModel().getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(z);
        }
    }

    private void onOpen(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileFilter(new FileNameExtensionFilter("MiniLang (*.minilang, *.txt)", "minilang", "txt"));
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            File file = chooser.getSelectedFile();
            try {
                String text = new String(java.nio.file.Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
                inputArea.setText(text);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo leer el archivo: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void onAnalyze(ActionEvent e) {
        try (Reader reader = new StringReader(inputArea.getText())) {
            LexerAdapter adapter = new LexerAdapter();
            LexerAdapter.Result result = adapter.run(reader);
            fillTable(result.tokens);
            fillErrors(result.errors);

            Parser parser = new Parser(new StringReader(inputArea.getText()));
            Program prog = parser.parse();

            new SemanticAnalyzer().check(prog); // si hay error semántico, lanza excepción

            // 3) Si todo ok, mostramos estado en el área de errores
            if (result.errors.isEmpty()) {
                errorArea.setForeground(new Color(0x065F46)); // verde ok
                errorArea.setText("Sin errores léxicos.\nSintaxis OK.\nSemántica OK.");
            } else {
                // ya se mostraron errores léxicos; pero igual indicamos que sintaxis/semántica pasaron
                errorArea.append("\nSintaxis OK.\nSemántica OK.");
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error durante el análisis: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void onExportCsv(ActionEvent e) {
        DefaultTableModel model = (DefaultTableModel) tokenTable.getModel();
        if (model.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No hay tokens para exportar.", "Aviso", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JFileChooser chooser = new JFileChooser();
        chooser.setSelectedFile(new File("tokens.csv"));
        if (chooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            File out = chooser.getSelectedFile();
            try (PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(out), StandardCharsets.UTF_8))) {
                pw.println("N,Tipo,Lexema,Linea,Columna");
                for (int i = 0; i < model.getRowCount(); i++) {
                    pw.printf("%s,%s,%s,%s,%s%n",
                            model.getValueAt(i, 0),
                            model.getValueAt(i, 1),
                            escapeCsv(String.valueOf(model.getValueAt(i, 2))),
                            model.getValueAt(i, 3),
                            model.getValueAt(i, 4));
                }
                JOptionPane.showMessageDialog(this, "CSV exportado correctamente.");
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "No se pudo exportar: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private static String escapeCsv(String s) {
        if (s.contains(",") || s.contains("\"") || s.contains("\n")) {
            return "\"" + s.replace("\"", "\"\"") + "\"";
        }
        return s;
    }

    private void fillTable(List<Token> tokens) {
        DefaultTableModel model = new DefaultTableModel(new Object[][]{}, new String[]{"#","Tipo","Lexema","Línea","Columna"}) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        int i = 1;
        for (Token t : tokens) {
            model.addRow(new Object[]{ i++, t.type, t.lexeme, t.line, t.column });
        }
        tokenTable.setModel(model);
        styleTable(tokenTable);
    }

    private void fillErrors(List<String> errors) {
        if (errors.isEmpty()) {
            errorArea.setText("Sin errores léxicos.");
            errorArea.setForeground(new Color(0x065F46)); // verde ok
        } else {
            errorArea.setText(String.join("\n", errors));
            errorArea.setForeground(new Color(180, 0, 0));
        }
    }

    private void clearAll() {
        inputArea.setText("");
        tokenTable.setModel(new DefaultTableModel(new Object[][]{}, new String[]{"#","Tipo","Lexema","Línea","Columna"}));
        styleTable(tokenTable);
        errorArea.setText("");
    }

    private void loadDemo() {
        String demo = "// Demo MiniLang\n" +
                "int x = 10;\n" +
                "float y = 2.5;\n" +
                "string msg = \"Hola mundo!\";\n" +
                "bool ok = true;\n\n" +
                "if (x >= 10 && ok) {\n" +
                "   y = y + 1.0;\n" +
                "} else {\n" +
                "   // comentario de línea\n" +
                "   y = y - 0.5;\n" +
                "}\n\n" +
                "/* comentario\n" +
                "   de bloque */\n" +
                "return y;\n";
        inputArea.setText(demo);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LexerRunner().setVisible(true));
    }
}
