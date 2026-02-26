package com.battleship.ui.lobby;

import com.battleship.client.ClientController;
import com.battleship.common.Protocol;
import com.battleship.ui.game.ShipPlacementGUI;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana de Sala de Espera con FlatLaf
 */
public class RoomWaitingGUI extends JFrame {

    private ClientController controller;
    private String playerName;
    private String roomId;

    private JPanel player1Panel;
    private JPanel player2Panel;
    private JLabel statusLabel;
    private JButton leaveButton;

    public RoomWaitingGUI(String playerName, String roomId) {
        this.playerName = playerName;
        this.roomId = roomId;
        this.controller = ClientController.getInstance();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();

        updatePlayerPanel(player1Panel, playerName, "Jugador 1", new Color(76, 175, 80));
        updatePlayerPanel(player2Panel, "Esperando...", "Jugador 2", new Color(120, 120, 120));
        statusLabel.setText("⏳ Estado: Esperando oponente");

        controller.setCurrentView(this);
    }

    public RoomWaitingGUI(String playerName, String roomId, String player1Name) {
        this.playerName = playerName;
        this.roomId = roomId;
        this.controller = ClientController.getInstance();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();

        updatePlayerPanel(player1Panel, player1Name, "Jugador 1", new Color(76, 175, 80));
        updatePlayerPanel(player2Panel, playerName, "Jugador 2", new Color(41, 98, 255));
        statusLabel.setText("✓ Estado: ¡Conectado!");

        controller.setCurrentView(this);
    }

    private void initComponents() {
        setTitle("⚓ Sala de Espera: " + roomId);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 450);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Título
        JLabel titleLabel = new JLabel("Sala de Espera: " + roomId, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 22));
        titleLabel.setForeground(new Color(41, 98, 255));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(10));

        JLabel subtitleLabel = new JLabel("Esperando oponente para comenzar", SwingConstants.CENTER);
        subtitleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(120, 120, 120));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        mainPanel.add(Box.createVerticalStrut(30));

        // Paneles de jugadores
        JPanel playersPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        playersPanel.setMaximumSize(new Dimension(600, 180));
        playersPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        player1Panel = createPlayerPanel("Jugador 1", new Color(76, 175, 80));
        player2Panel = createPlayerPanel("Jugador 2", new Color(41, 98, 255));

        playersPanel.add(player1Panel);
        playersPanel.add(player2Panel);
        mainPanel.add(playersPanel);
        mainPanel.add(Box.createVerticalStrut(25));

        // Estado
        statusLabel = new JLabel("⏳ Estado: Esperando oponente", SwingConstants.CENTER);
        statusLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusLabel.setForeground(new Color(120, 120, 120));
        statusLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        statusLabel.setOpaque(true);
        statusLabel.setBackground(Color.WHITE);
        statusLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(statusLabel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Botón salir
        leaveButton = new JButton("Abandonar Sala");
        leaveButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        leaveButton.setMaximumSize(new Dimension(180, 40));
        leaveButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        leaveButton.addActionListener(e -> handleLeaveRoom());
        mainPanel.add(leaveButton);

        add(mainPanel);
    }

    private JPanel createPlayerPanel(String title, Color color) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 2, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        panel.setBackground(Color.WHITE);

        String icon = title.equals("Jugador 1") ? "⚓" : "⛵";
        JLabel iconLabel = new JLabel(icon, SwingConstants.CENTER);
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 36));
        iconLabel.setForeground(color);
        iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        iconLabel.setName("iconLabel");
        panel.add(iconLabel);
        panel.add(Box.createVerticalStrut(10));

        JLabel titleLabel = new JLabel(title, SwingConstants.CENTER);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        titleLabel.setForeground(color);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setName("titleLabel");
        panel.add(titleLabel);
        panel.add(Box.createVerticalStrut(8));

        JLabel nameLabel = new JLabel("", SwingConstants.CENTER);
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        nameLabel.setForeground(new Color(50, 50, 50));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        nameLabel.setName("nameLabel");
        panel.add(nameLabel);

        return panel;
    }

    private void updatePlayerPanel(JPanel panel, String playerName, String title, Color color) {
        // Buscar componentes por nombre en lugar de por índice
        JLabel titleLabel = null;
        JLabel nameLabel = null;

        for (Component comp : panel.getComponents()) {
            if (comp instanceof JLabel) {
                JLabel label = (JLabel) comp;
                if ("titleLabel".equals(label.getName())) {
                    titleLabel = label;
                } else if ("nameLabel".equals(label.getName())) {
                    nameLabel = label;
                }
            }
        }

        if (titleLabel != null) {
            titleLabel.setText(title);
            titleLabel.setForeground(color);
        }

        if (nameLabel != null) {
            nameLabel.setText(playerName);
        }

        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(color, 3, true),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
    }

    public void onPlayerJoined(String player2Name) {
        SwingUtilities.invokeLater(() -> {
            updatePlayerPanel(player2Panel, player2Name, "Jugador 2", new Color(41, 98, 255));
            statusLabel.setText("✓ Estado: ¡Oponente conectado!");
            statusLabel.setForeground(new Color(76, 175, 80));
            statusLabel.setBackground(new Color(245, 255, 245));
            statusLabel.setBorder(BorderFactory.createLineBorder(new Color(76, 175, 80), 1));
        });
    }

    public void onStartPlacingShips() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("⚔ Estado: ¡Comenzando batalla naval!");
            statusLabel.setForeground(new Color(76, 175, 80));
            leaveButton.setEnabled(false);
            leaveButton.setText("Iniciando...");

            dispose();
            ShipPlacementGUI shipPlacement = new ShipPlacementGUI(playerName);
            shipPlacement.setVisible(true);
        });
    }

    private void handleLeaveRoom() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Seguro que quieres abandonar la sala?",
            "Abandonar Sala",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.sendMessage(Protocol.LOGOUT);
            dispose();

            int victorias = controller.getVictorias();
            int derrotas = controller.getDerrotas();
            LobbyGUI lobby = new LobbyGUI(playerName, victorias, derrotas);
            lobby.setVisible(true);
        }
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getRoomId() {
        return roomId;
    }
}
