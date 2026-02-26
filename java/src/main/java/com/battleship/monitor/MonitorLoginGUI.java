package com.battleship.monitor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de Login mejorada específica para el Monitor (Administrador).
 * Valida credenciales antes de abrir el MonitorGUI.
 */
public class MonitorLoginGUI extends JFrame {

    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JLabel statusLabel;

    // Credenciales de administrador (hardcoded por seguridad del servidor)
    private static final String ADMIN_USER = "ADMIN";
    private static final String ADMIN_PASS = "admin123";

    public MonitorLoginGUI() {
        initComponents();
    }

    private void initComponents() {
        setTitle("Monitor Batalla Naval - Login Administrador");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 350);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con padding
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));
        mainPanel.setBackground(new Color(240, 240, 245));

        // Panel del título
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(new Color(240, 240, 245));

        JLabel titleLabel = new JLabel("MONITOR - ADMINISTRADOR");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitleLabel = new JLabel("Sistema de Monitoreo Batalla Naval");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        subtitleLabel.setForeground(new Color(108, 117, 125));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        headerPanel.add(titleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        headerPanel.add(subtitleLabel);
        headerPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // Panel del formulario
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(206, 212, 218), 1),
            BorderFactory.createEmptyBorder(30, 30, 30, 30)
        ));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);

        // Usuario
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 0.3;
        formPanel.add(userLabel, gbc);

        userField = new JTextField(15);
        userField.setFont(new Font("Arial", Font.PLAIN, 13));
        userField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 0;
        gbc.weightx = 0.7;
        formPanel.add(userField, gbc);

        // Contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Arial", Font.BOLD, 13));
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 0.3;
        formPanel.add(passwordLabel, gbc);

        passwordField = new JPasswordField(15);
        passwordField.setFont(new Font("Arial", Font.PLAIN, 13));
        passwordField.setPreferredSize(new Dimension(200, 30));
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.weightx = 0.7;
        formPanel.add(passwordField, gbc);

        // Agregar listener para Enter
        KeyAdapter enterKeyListener = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    handleLogin();
                }
            }
        };
        userField.addKeyListener(enterKeyListener);
        passwordField.addKeyListener(enterKeyListener);

        // Botón de login
        loginButton = new JButton("INICIAR SESIÓN");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setPreferredSize(new Dimension(200, 40));
        loginButton.setBackground(new Color(0, 123, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> handleLogin());

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(20, 5, 5, 5);
        formPanel.add(loginButton, gbc);

        // Label de estado
        statusLabel = new JLabel(" ");
        statusLabel.setFont(new Font("Arial", Font.ITALIC, 11));
        statusLabel.setForeground(Color.RED);
        statusLabel.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(5, 5, 5, 5);
        formPanel.add(statusLabel, gbc);

        // Panel inferior con info
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(new Color(240, 240, 245));
        JLabel infoLabel = new JLabel("Acceso restringido solo para administradores");
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        infoLabel.setForeground(new Color(108, 117, 125));
        footerPanel.add(infoLabel);

        // Agregar todo al panel principal
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(formPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);

        add(mainPanel);

        // Focus inicial en el campo de usuario
        SwingUtilities.invokeLater(() -> userField.requestFocusInWindow());
    }

    /**
     * Maneja el intento de login
     */
    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor ingrese usuario y contraseña");
            return;
        }

        // Validar credenciales
        if (username.equals(ADMIN_USER) && password.equals(ADMIN_PASS)) {
            // Login exitoso
            statusLabel.setForeground(new Color(40, 167, 69));
            statusLabel.setText("Autenticación exitosa. Iniciando monitor...");
            loginButton.setEnabled(false);

            // Abrir MonitorGUI después de un breve delay
            SwingUtilities.invokeLater(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException ex) {
                    ex.printStackTrace();
                }
                openMonitorGUI();
            });
        } else {
            showError("Usuario o contraseña incorrectos");
            passwordField.setText("");
            passwordField.requestFocusInWindow();
        }
    }

    /**
     * Muestra un mensaje de error
     */
    private void showError(String message) {
        statusLabel.setForeground(Color.RED);
        statusLabel.setText(message);

        // Efecto de "shake" en el formulario (opcional)
        Timer timer = new Timer(50, null);
        final int[] shakeCount = {0};
        timer.addActionListener(e -> {
            if (shakeCount[0] < 4) {
                int offset = (shakeCount[0] % 2 == 0) ? 5 : -5;
                setLocation(getX() + offset, getY());
                shakeCount[0]++;
            } else {
                ((Timer) e.getSource()).stop();
            }
        });
        timer.start();
    }

    /**
     * Abre el MonitorGUI y cierra esta ventana
     */
    private void openMonitorGUI() {
        // Crear y mostrar el MonitorGUI
        SwingUtilities.invokeLater(() -> {
            MonitorGUI monitor = new MonitorGUI();
            monitor.setVisible(true);

            // Cerrar esta ventana de login
            dispose();
        });
    }

    public static void main(String[] args) {
        // Configurar Look and Feel del sistema
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Lanzar GUI en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            MonitorLoginGUI loginGUI = new MonitorLoginGUI();
            loginGUI.setVisible(true);
        });
    }
}
