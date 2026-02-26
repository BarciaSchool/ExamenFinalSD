package com.battleship.ui.login;

import com.battleship.client.ClientController;
import com.battleship.common.Protocol;
import com.battleship.config.Config;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Ventana de Login con FlatLaf - Diseño moderno automático
 */
public class LoginGUI extends JFrame {

    private JTextField userField;
    private JPasswordField passwordField;
    private JButton loginButton;
    private JButton registerButton;
    private JLabel statusLabel;

    public LoginGUI() {
        // Configurar FlatLaf para look moderno
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();
    }

    private void initComponents() {
        setTitle("⚓ Batalla Naval - Login");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(420, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Título
        JLabel titleLabel = new JLabel("BATALLA NAVAL", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Inicia sesión para comenzar", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Campo Usuario
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(userLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        userField = new JTextField(20);
        userField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(userField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Campo Contraseña
        JLabel passwordLabel = new JLabel("Contraseña:");
        passwordLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(passwordLabel);
        mainPanel.add(Box.createVerticalStrut(5));

        passwordField = new JPasswordField(20);
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(15));

        // Checkbox mostrar contraseña
        JCheckBox showPasswordCheckBox = new JCheckBox("Mostrar contraseña");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        showPasswordCheckBox.addItemListener(e -> {
            if (e.getStateChange() == java.awt.event.ItemEvent.SELECTED) {
                passwordField.setEchoChar((char) 0);
            } else {
                passwordField.setEchoChar('•');
            }
        });
        mainPanel.add(showPasswordCheckBox);
        mainPanel.add(Box.createVerticalStrut(20));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setMaximumSize(new Dimension(300, 50));

        loginButton = new JButton("Iniciar Sesión");
        loginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        loginButton.addActionListener(e -> handleLogin());

        registerButton = new JButton("Registrar");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.addActionListener(e -> handleRegister());

        buttonPanel.add(loginButton);
        buttonPanel.add(registerButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(20));

        // Label de estado
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        // Panel inferior
        JLabel infoLabel = new JLabel("¿No tienes cuenta? Presiona Registrar");
        infoLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(120, 120, 120));
        infoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(infoLabel);

        add(mainPanel);

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

        // Focus inicial
        SwingUtilities.invokeLater(() -> userField.requestFocusInWindow());
    }

    private void handleLogin() {
        String username = userField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        // Validar campos vacíos
        if (username.isEmpty() || password.isEmpty()) {
            showError("Por favor ingrese usuario y contraseña");
            return;
        }

        // Validar longitud mínima
        if (username.length() < 3) {
            showError("El usuario debe tener al menos 3 caracteres");
            return;
        }

        if (password.length() < 6) {
            showError("La contraseña debe tener al menos 6 caracteres");
            return;
        }

        // Conectar al servidor
        ClientController controller = ClientController.getInstance();
        String serverHost = Config.getServerHost();
        int serverPort = Config.getServerPort();

        boolean connected = controller.connect(serverHost, serverPort);

        if (!connected) {
            showError("No se pudo conectar al servidor");
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar al servidor.\n" +
                    "Verifica que:\n" +
                    "1. El servidor esté corriendo en " + Config.getServerAddress() + "\n" +
                    "2. Ejecutaste network-setup.bat en esta PC\n" +
                    "3. El firewall no bloquee el puerto 9090",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        controller.setCurrentView(this);
        controller.setPlayerName(username);
        controller.sendMessage(Protocol.LOGIN + ":" + username + ":" + password);

        showInfo("Conectando...");
        loginButton.setEnabled(false);
        registerButton.setEnabled(false);
    }

    private void handleRegister() {
        dispose();
        new RegisterGUI().setVisible(true);
    }

    private void showError(String message) {
        statusLabel.setText("⚠ " + message);
        statusLabel.setForeground(new Color(220, 53, 69));
    }

    private void showInfo(String message) {
        statusLabel.setText("✓ " + message);
        statusLabel.setForeground(new Color(25, 135, 84));
    }

    public void showServerError(String message) {
        loginButton.setEnabled(true);
        registerButton.setEnabled(true);
        showError(message);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginGUI().setVisible(true));
    }
}
