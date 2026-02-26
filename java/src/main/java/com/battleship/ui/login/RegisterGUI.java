package com.battleship.ui.login;

import com.battleship.client.ClientController;
import com.battleship.common.Protocol;
import com.battleship.validation.InputValidator;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;

/**
 * Ventana de Registro con FlatLaf - Diseño moderno simple
 */
public class RegisterGUI extends JFrame {

    private JTextField usernameField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private JTextField nombreField;
    private JTextField apellidoField;
    private JComboBox<String> avatarCombo;
    private JButton registerButton;
    private JButton backToLoginButton;
    private JLabel statusLabel;

    public RegisterGUI() {
        // Configurar FlatLaf
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();
    }

    private void initComponents() {
        setTitle("⛵ Batalla Naval - Registro");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(450, 650);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal con scroll
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 35, 25, 35));

        // Título
        JLabel titleLabel = new JLabel("CREAR CUENTA", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 26));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(8));

        JLabel subtitleLabel = new JLabel("Únete a la flota naval", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Campo Usuario
        JLabel userLabel = new JLabel("Usuario:");
        userLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(userLabel);
        mainPanel.add(Box.createVerticalStrut(4));

        usernameField = new JTextField(20);
        usernameField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(usernameField);
        mainPanel.add(Box.createVerticalStrut(12));

        // Campo Contraseña
        JLabel passLabel = new JLabel("Contraseña:");
        passLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(passLabel);
        mainPanel.add(Box.createVerticalStrut(4));

        passwordField = new JPasswordField(20);
        passwordField.setEchoChar('•');
        passwordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(passwordField);
        mainPanel.add(Box.createVerticalStrut(12));

        // Campo Confirmar Contraseña
        JLabel confirmLabel = new JLabel("Confirmar Contraseña:");
        confirmLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(confirmLabel);
        mainPanel.add(Box.createVerticalStrut(4));

        confirmPasswordField = new JPasswordField(20);
        confirmPasswordField.setEchoChar('•');
        confirmPasswordField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(confirmPasswordField);
        mainPanel.add(Box.createVerticalStrut(12));

        // Checkbox mostrar contraseñas
        JCheckBox showPasswordCheckBox = new JCheckBox("Mostrar contraseñas");
        showPasswordCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        showPasswordCheckBox.addItemListener(e -> {
            char echoChar = e.getStateChange() == ItemEvent.SELECTED ? (char) 0 : '•';
            passwordField.setEchoChar(echoChar);
            confirmPasswordField.setEchoChar(echoChar);
        });
        mainPanel.add(showPasswordCheckBox);
        mainPanel.add(Box.createVerticalStrut(12));

        // Campo Nombre
        JLabel nombreLabel = new JLabel("Nombre (opcional):");
        nombreLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(nombreLabel);
        mainPanel.add(Box.createVerticalStrut(4));

        nombreField = new JTextField(20);
        nombreField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(nombreField);
        mainPanel.add(Box.createVerticalStrut(12));

        // Campo Apellido
        JLabel apellidoLabel = new JLabel("Apellido (opcional):");
        apellidoLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(apellidoLabel);
        mainPanel.add(Box.createVerticalStrut(4));

        apellidoField = new JTextField(20);
        apellidoField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(apellidoField);
        mainPanel.add(Box.createVerticalStrut(12));

        // Campo Avatar
        JLabel avatarLabel = new JLabel("Avatar:");
        avatarLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        mainPanel.add(avatarLabel);
        mainPanel.add(Box.createVerticalStrut(4));

        String[] avatars = {"⚓ Almirante", "⛵ Capitán", "🚢 Comandante", "🌊 Marinero", "🧭 Navegador"};
        avatarCombo = new JComboBox<>(avatars);
        avatarCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        mainPanel.add(avatarCombo);
        mainPanel.add(Box.createVerticalStrut(12));

        // Checkbox términos
        JCheckBox acceptTermsCheckBox = new JCheckBox("Acepto los términos y condiciones");
        acceptTermsCheckBox.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        mainPanel.add(acceptTermsCheckBox);
        mainPanel.add(Box.createVerticalStrut(15));

        // Botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        buttonPanel.setMaximumSize(new Dimension(300, 50));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        registerButton = new JButton("Crear Cuenta");
        registerButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        registerButton.addActionListener(e -> handleRegister(acceptTermsCheckBox.isSelected()));

        backToLoginButton = new JButton("Volver");
        backToLoginButton.setFont(new Font("Segoe UI", Font.BOLD, 13));
        backToLoginButton.addActionListener(e -> backToLogin());

        buttonPanel.add(registerButton);
        buttonPanel.add(backToLoginButton);
        mainPanel.add(buttonPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Label de estado
        statusLabel = new JLabel(" ", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.ITALIC, 11));
        statusLabel.setForeground(new Color(220, 53, 69));
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);

        // Scroll panel por si el contenido es muy largo
        JScrollPane scrollPane = new JScrollPane(mainPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane);

        // Focus inicial
        SwingUtilities.invokeLater(() -> usernameField.requestFocusInWindow());
    }

    private void handleRegister(boolean acceptTerms) {
        statusLabel.setText(" ");

        if (!acceptTerms) {
            showError("Debes aceptar los términos y condiciones");
            return;
        }

        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());
        String nombre = nombreField.getText().trim();
        String apellido = apellidoField.getText().trim();
        String avatar = (String) avatarCombo.getSelectedItem();

        InputValidator.ValidationResult usernameResult = InputValidator.validateUsername(username);
        if (!usernameResult.isValid()) {
            showError(usernameResult.getErrorMessage());
            return;
        }

        InputValidator.ValidationResult passwordResult = InputValidator.validatePassword(password);
        if (!passwordResult.isValid()) {
            showError(passwordResult.getErrorMessage());
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Las contraseñas no coinciden");
            return;
        }

        InputValidator.ValidationResult nombreResult = InputValidator.validateName(nombre);
        if (!nombreResult.isValid()) {
            showError(nombreResult.getErrorMessage());
            return;
        }

        InputValidator.ValidationResult apellidoResult = InputValidator.validateName(apellido);
        if (!apellidoResult.isValid()) {
            showError(apellidoResult.getErrorMessage());
            return;
        }

        if (avatar != null && avatar.contains(" ")) {
            avatar = avatar.split(" ", 2)[1].toLowerCase();
        }

        if (nombre.isEmpty()) nombre = "Jugador";
        if (apellido.isEmpty()) apellido = "";

        ClientController controller = ClientController.getInstance();
        if (!controller.isConnected()) {
            boolean connected = controller.connect("localhost", 9090);
            if (!connected) {
                showError("No se pudo conectar al servidor");
                return;
            }
        }

        controller.setCurrentView(this);
        registerButton.setEnabled(false);
        registerButton.setText("Registrando...");

        String registerMessage = Protocol.REGISTER + ":" + username + ":" + password + ":" + nombre + ":" + apellido + ":" + avatar;
        controller.sendMessage(registerMessage);
    }

    private void backToLogin() {
        dispose();
        new LoginGUI().setVisible(true);
    }

    private void showError(String message) {
        statusLabel.setText("⚠ " + message);
        statusLabel.setForeground(new Color(220, 53, 69));
    }

    private void showSuccess(String message) {
        statusLabel.setText("✓ " + message);
        statusLabel.setForeground(new Color(25, 135, 84));
    }

    public void showRegistrationError(String error) {
        registerButton.setEnabled(true);
        registerButton.setText("Crear Cuenta");
        showError(error);
    }

    public void handleRegistrationSuccess() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(
                    this,
                    "Registro exitoso. Por favor inicia sesión.",
                    "Éxito",
                    JOptionPane.INFORMATION_MESSAGE
            );
            backToLogin();
        });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new RegisterGUI().setVisible(true));
    }
}
