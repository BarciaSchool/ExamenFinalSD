package com.battleship.ui.components;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * Componentes UI reutilizables con diseño moderno consistente
 * para el proyecto Batalla Naval.
 */
public class ModernComponents {

    // Paleta de colores consistente
    public static class Colors {
        public static final Color PRIMARY = new Color(41, 98, 255);
        public static final Color PRIMARY_DARK = new Color(30, 64, 175);
        public static final Color SECONDARY = new Color(76, 175, 80);
        public static final Color ERROR = new Color(244, 67, 54);
        public static final Color SUCCESS = new Color(76, 175, 80);
        public static final Color WARNING = new Color(255, 152, 0);
        public static final Color INFO = new Color(33, 150, 243);
        public static final Color BACKGROUND = new Color(248, 250, 252);
        public static final Color CARD = new Color(255, 255, 255);
        public static final Color TEXT = new Color(33, 37, 41);
        public static final Color TEXT_MUTED = new Color(108, 117, 125);
        public static final Color FIELD = new Color(248, 249, 250);
        public static final Color BORDER = new Color(206, 212, 218);
    }

    // Fuentes consistentes
    public static class Fonts {
        public static final Font TITLE = new Font("Segoe UI", Font.BOLD, 28);
        public static final Font SUBTITLE = new Font("Segoe UI", Font.PLAIN, 16);
        public static final Font LABEL = new Font("Segoe UI", Font.BOLD, 14);
        public static final Font FIELD = new Font("Segoe UI", Font.PLAIN, 14);
        public static final Font BUTTON = new Font("Segoe UI", Font.BOLD, 14);
        public static final Font STATUS = new Font("Segoe UI", Font.ITALIC, 12);
        public static final Font SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    }

    /**
     * Crea un campo de texto con estilo moderno
     */
    public static JTextField createModernTextField(String placeholder) {
        JTextField field = new JTextField(placeholder);
        styleField(field);
        return field;
    }

    /**
     * Crea un campo de contraseña con estilo moderno
     */
    public static JPasswordField createModernPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setEchoChar('•');
        styleField(field);
        return field;
    }

    /**
     * Aplica estilo consistente a campos de texto
     */
    public static void styleField(JTextField field) {
        field.setFont(Fonts.FIELD);
        field.setForeground(Colors.TEXT);
        field.setCaretColor(Colors.PRIMARY);
        field.setBackground(Colors.FIELD);
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colors.BORDER, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
        field.setPreferredSize(new Dimension(0, 42));

        // Focus effects
        field.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Colors.PRIMARY, 2),
                    new EmptyBorder(11, 14, 11, 14)
                ));
            }

            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                field.setBorder(BorderFactory.createCompoundBorder(
                    new LineBorder(Colors.BORDER, 1),
                    new EmptyBorder(12, 15, 12, 15)
                ));
            }
        });
    }

    /**
     * Aplica borde de validación exitosa
     */
    public static void setSuccessBorder(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colors.SUCCESS, 2),
            new EmptyBorder(11, 14, 11, 14)
        ));
    }

    /**
     * Aplica borde de validación fallida
     */
    public static void setErrorBorder(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colors.ERROR, 2),
            new EmptyBorder(11, 14, 11, 14)
        ));
    }

    /**
     * Restaura borde normal
     */
    public static void setNormalBorder(JTextField field) {
        field.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colors.BORDER, 1),
            new EmptyBorder(12, 15, 12, 15)
        ));
    }

    /**
     * Crea un botón con estilo moderno
     */
    public static JButton createModernButton(String text, Color backgroundColor) {
        JButton button = new JButton(text);
        button.setFont(Fonts.BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(0, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
        });

        return button;
    }

    /**
     * Crea un botón primario
     */
    public static JButton createPrimaryButton(String text) {
        return createModernButton(text, Colors.PRIMARY);
    }

    /**
     * Crea un botón secundario
     */
    public static JButton createSecondaryButton(String text) {
        return createModernButton(text, Colors.SECONDARY);
    }

    /**
     * Crea un botón de peligro
     */
    public static JButton createDangerButton(String text) {
        return createModernButton(text, Colors.ERROR);
    }

    /**
     * Crea un panel de tarjeta moderno
     */
    public static JPanel createCardPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(Colors.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }

    /**
     * Crea un panel de tarjeta con padding personalizado
     */
    public static JPanel createCardPanel(int padding) {
        JPanel panel = new JPanel();
        panel.setBackground(Colors.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(229, 231, 235), 1, true),
            new EmptyBorder(padding, padding, padding, padding)
        ));
        return panel;
    }

    /**
     * Crea una etiqueta de título
     */
    public static JLabel createTitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.TITLE);
        label.setForeground(Colors.PRIMARY);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta de subtítulo
     */
    public static JLabel createSubtitleLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.SUBTITLE);
        label.setForeground(Colors.TEXT_MUTED);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        return label;
    }

    /**
     * Crea una etiqueta de campo
     */
    public static JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.LABEL);
        label.setForeground(Colors.TEXT);
        return label;
    }

    /**
     * Crea una etiqueta de estado
     */
    public static JLabel createStatusLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(Fonts.STATUS);
        label.setForeground(Colors.ERROR);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

    /**
     * Crea un JComboBox con estilo moderno
     */
    public static <T> JComboBox<T> createModernComboBox(T[] items) {
        JComboBox<T> comboBox = new JComboBox<>(items);
        comboBox.setFont(Fonts.FIELD);
        comboBox.setBackground(Colors.FIELD);
        comboBox.setForeground(Colors.TEXT);
        comboBox.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colors.BORDER, 1),
            new EmptyBorder(8, 15, 8, 15)
        ));
        comboBox.setPreferredSize(new Dimension(0, 42));
        return comboBox;
    }

    /**
     * Crea un JCheckBox con estilo moderno
     */
    public static JCheckBox createModernCheckBox(String text) {
        JCheckBox checkBox = new JCheckBox(text);
        checkBox.setFont(Fonts.SMALL);
        checkBox.setForeground(Colors.TEXT_MUTED);
        checkBox.setBackground(Colors.CARD);
        checkBox.setOpaque(false);
        return checkBox;
    }

    /**
     * Crea un panel de overlay de carga
     */
    public static JPanel createLoadingOverlay(String message) {
        JPanel overlay = new JPanel(new BorderLayout());
        overlay.setBackground(new Color(0, 0, 0, 100));
        overlay.setOpaque(true);

        JPanel loadingPanel = new JPanel();
        loadingPanel.setBackground(Colors.CARD);
        loadingPanel.setBorder(new EmptyBorder(30, 50, 30, 50));
        loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.Y_AXIS));

        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(200, 4));

        JLabel loadingLabel = new JLabel(message);
        loadingLabel.setFont(Fonts.FIELD);
        loadingLabel.setForeground(Colors.TEXT);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loadingPanel.add(progressBar);
        loadingPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        loadingPanel.add(loadingLabel);

        overlay.add(loadingPanel, BorderLayout.CENTER);
        return overlay;
    }

    /**
     * Crea una ventana con bordes redondeados
     */
    public static void setupRoundedWindow(JFrame frame, int width, int height) {
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, width, height, 20, 20));
        frame.getContentPane().setBackground(Colors.BACKGROUND);
    }

    /**
     * Configura el arrastre de ventana sin decoración
     */
    public static void setupWindowDrag(JFrame frame, JPanel dragPanel) {
        final int[] pos = new int[2];
        final int[] dragPos = new int[2];

        dragPanel.addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                pos[0] = e.getXOnScreen();
                pos[1] = e.getYOnScreen();
                dragPos[0] = frame.getX();
                dragPos[1] = frame.getY();
            }
        });

        dragPanel.addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                int x = pos[0] - e.getXOnScreen() + dragPos[0];
                int y = pos[1] - e.getYOnScreen() + dragPos[1];
                frame.setLocation(x, y);
            }
        });
    }

    /**
     * Crea un panel con gradiente de fondo
     */
    public static JPanel createGradientPanel() {
        return new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(240, 248, 255),
                    0, getHeight(), new Color(230, 240, 250)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
    }

    /**
     * Crea un panel de campo con icono y etiqueta
     */
    public static JPanel createFieldPanel(String icon, String labelText, JTextField field) {
        JPanel fieldPanel = new JPanel(new BorderLayout());
        fieldPanel.setBackground(Colors.CARD);

        JLabel label = new JLabel(icon + " " + labelText);
        label.setFont(Fonts.LABEL);
        label.setForeground(Colors.TEXT);
        label.setBorder(new EmptyBorder(0, 0, 8, 0));

        styleField(field);

        fieldPanel.add(label, BorderLayout.NORTH);
        fieldPanel.add(field, BorderLayout.CENTER);

        return fieldPanel;
    }

    /**
     * Muestra un diálogo de confirmación moderno
     */
    public static boolean showConfirmDialog(Component parent, String title, String message) {
        return JOptionPane.showConfirmDialog(
            parent,
            message,
            title,
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        ) == JOptionPane.YES_OPTION;
    }

    /**
     * Muestra un diálogo de información moderno
     */
    public static void showInfoDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.INFORMATION_MESSAGE
        );
    }

    /**
     * Muestra un diálogo de error moderno
     */
    public static void showErrorDialog(Component parent, String title, String message) {
        JOptionPane.showMessageDialog(
            parent,
            message,
            title,
            JOptionPane.ERROR_MESSAGE
        );
    }

    /**
     * Crea un panel de progreso circular
     */
    public static JProgressBar createCircularProgress() {
        JProgressBar progressBar = new JProgressBar();
        progressBar.setIndeterminate(true);
        progressBar.setPreferredSize(new Dimension(40, 40));
        progressBar.setBorderPainted(false);
        progressBar.setOpaque(false);
        return progressBar;
    }

    /**
     * Crea un panel de iconos con texto (para stats, info, etc.)
     */
    public static JPanel createIconPanel(String icon, String title, String value, Color color) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Colors.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(color, 2, true),
            new EmptyBorder(15, 15, 15, 15)
        ));
        panel.setMaximumSize(new Dimension(150, 80));

        JLabel iconLabel = new JLabel(icon);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        iconLabel.setForeground(color);

        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Fonts.SMALL);
        titleLabel.setForeground(Colors.TEXT_MUTED);

        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(Fonts.LABEL);
        valueLabel.setForeground(Colors.TEXT);

        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);

        panel.add(iconLabel, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea una etiqueta de instrucción con ícono
     */
    public static JLabel createInstructionLabel(String text) {
        JLabel label = new JLabel("💡 " + text);
        label.setFont(Fonts.FIELD);
        label.setForeground(Colors.TEXT_MUTED);
        label.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(new Color(255, 243, 224), 1),
            new EmptyBorder(10, 15, 10, 15)
        ));
        label.setOpaque(true);
        label.setBackground(new Color(255, 252, 245));
        return label;
    }

    /**
     * Crea un panel de tarjeta con título
     */
    public static JPanel createTitledCard(String title) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(Colors.CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            new LineBorder(Colors.BORDER, 1, true),
            new EmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(Fonts.LABEL);
        titleLabel.setForeground(Colors.PRIMARY);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));

        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setOpaque(false);

        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(contentPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea un panel de estadísticas del juego
     */
    public static JPanel createGameStatsPanel(String title, String[] labels, String[] values) {
        JPanel panel = createTitledCard(title);

        JPanel statsPanel = new JPanel(new GridLayout(labels.length, 1, 5, 5));
        statsPanel.setOpaque(false);

        for (int i = 0; i < labels.length; i++) {
            JPanel statRow = new JPanel(new BorderLayout(10, 0));
            statRow.setOpaque(false);

            JLabel label = new JLabel(labels[i]);
            label.setFont(Fonts.SMALL);
            label.setForeground(Colors.TEXT_MUTED);

            JLabel value = new JLabel(values[i]);
            value.setFont(Fonts.LABEL);
            value.setForeground(Colors.TEXT);
            value.setHorizontalAlignment(SwingConstants.RIGHT);

            statRow.add(label, BorderLayout.WEST);
            statRow.add(value, BorderLayout.EAST);
            statsPanel.add(statRow);
        }

        JPanel contentPanel = (JPanel) panel.getComponent(1);
        contentPanel.add(statsPanel, BorderLayout.CENTER);

        return panel;
    }

    /**
     * Crea una etiqueta de badge (pequeño tag de estado)
     */
    public static JLabel createBadge(String text, Color backgroundColor) {
        JLabel label = new JLabel(text, SwingConstants.CENTER);
        label.setFont(new Font("Segoe UI", Font.BOLD, 11));
        label.setForeground(Color.WHITE);
        label.setOpaque(true);
        label.setBackground(backgroundColor);
        label.setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
        return label;
    }

    /**
     * Crea un botón con icono y texto
     */
    public static JButton createIconButton(String icon, String text, Color backgroundColor) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(Fonts.BUTTON);
        button.setForeground(Color.WHITE);
        button.setBackground(backgroundColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setPreferredSize(new Dimension(0, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }

            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(backgroundColor);
            }

            @Override
            public void mousePressed(MouseEvent e) {
                button.setBackground(backgroundColor.darker());
            }
        });

        return button;
    }

    /**
     * Crea un separador horizontal con título
     */
    public static JSeparator createTitledSeparator(String title) {
        JSeparator separator = new JSeparator();
        // Podríamos agregar más personalización aquí si es necesario
        return separator;
    }
}