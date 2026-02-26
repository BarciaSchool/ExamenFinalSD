package com.battleship.ui.lobby;

import com.battleship.client.ClientController;
import com.battleship.common.Protocol;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Ventana de Lobby con FlatLaf - Diseño moderno simple
 */
public class LobbyGUI extends JFrame {

    private ClientController controller;
    private DefaultTableModel tableModel;
    private JTable roomTable;
    private JLabel playerStatsLabel;
    private JButton createRoomButton;
    private JButton joinRoomButton;
    private JButton refreshButton;
    private JButton logoutButton;

    private String playerName;
    private int victorias;
    private int derrotas;

    public LobbyGUI(String playerName, int victorias, int derrotas) {
        this.playerName = playerName;
        this.victorias = victorias;
        this.derrotas = derrotas;
        this.controller = ClientController.getInstance();

        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            e.printStackTrace();
        }

        initComponents();
        requestRoomList();
    }

    private void initComponents() {
        setTitle("🎮 Batalla Naval - Lobby");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);
        setResizable(false);

        // Panel principal
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));

        // Panel superior con info del jugador
        JPanel headerPanel = new JPanel(new BorderLayout(10, 10));
        headerPanel.setMaximumSize(new Dimension(800, 80));

        JPanel welcomePanel = new JPanel();
        welcomePanel.setLayout(new BoxLayout(welcomePanel, BoxLayout.Y_AXIS));
        welcomePanel.setOpaque(false);

        JLabel iconLabel = new JLabel("⚓");
        iconLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        iconLabel.setForeground(new Color(41, 98, 255));

        JLabel welcomeLabel = new JLabel("Bienvenido, Almirante " + playerName);
        welcomeLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        welcomeLabel.setForeground(new Color(41, 98, 255));

        welcomePanel.add(welcomeLabel);
        welcomePanel.add(Box.createVerticalStrut(5));

        playerStatsLabel = new JLabel(String.format(
            "Historial: Victorias: %d | Derrotas: %d | Ratio: %.2f%%",
            victorias, derrotas, calcularRatio(victorias, derrotas)
        ));
        playerStatsLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        playerStatsLabel.setForeground(new Color(120, 120, 120));
        welcomePanel.add(playerStatsLabel);

        JPanel leftPanel = new JPanel(new BorderLayout(5, 0));
        leftPanel.setOpaque(false);
        leftPanel.add(iconLabel, BorderLayout.WEST);
        leftPanel.add(welcomePanel, BorderLayout.CENTER);

        logoutButton = new JButton("Cerrar Sesión");
        logoutButton.setFont(new Font("Segoe UI", Font.BOLD, 12));
        logoutButton.addActionListener(e -> handleLogout());

        headerPanel.add(leftPanel, BorderLayout.CENTER);
        headerPanel.add(logoutButton, BorderLayout.EAST);
        mainPanel.add(headerPanel);
        mainPanel.add(Box.createVerticalStrut(15));

        // Tabla de salas
        JLabel tableTitle = new JLabel("📋 Salas Disponibles (Máximo 4 salas activas)");
        tableTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.add(tableTitle);
        mainPanel.add(Box.createVerticalStrut(10));

        String[] columnNames = {"ID", "Jugador 1", "Jugador 2", "Estado", "Jugadores"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roomTable.setRowHeight(32);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 12));

        // Renderer para colorear estados
        roomTable.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

                if (!isSelected && column == 3) {
                    String estado = value.toString();
                    switch (estado) {
                        case "Esperando":
                            setBackground(new Color(200, 230, 201));
                            setForeground(new Color(0, 100, 0));
                            setText("⏳ " + estado);
                            break;
                        case "Colocando Barcos":
                            setBackground(new Color(255, 243, 224));
                            setForeground(new Color(180, 100, 0));
                            setText("⚙ " + estado);
                            break;
                        case "Jugando":
                            setBackground(new Color(227, 242, 253));
                            setForeground(new Color(0, 100, 180));
                            setText("⚔ " + estado);
                            break;
                        case "Terminado":
                            setBackground(new Color(245, 245, 245));
                            setForeground(new Color(150, 150, 150));
                            setText("✓ " + estado);
                            break;
                    }
                } else if (!isSelected) {
                    setBackground(Color.WHITE);
                    setForeground(Color.BLACK);
                }
                setHorizontalAlignment(SwingConstants.CENTER);
                return c;
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(roomTable);
        tableScrollPane.setMaximumSize(new Dimension(800, 250));
        tableScrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(tableScrollPane);
        mainPanel.add(Box.createVerticalStrut(15));

        // Panel de botones
        JPanel buttonPanel = new JPanel(new GridLayout(1, 3, 15, 0));
        buttonPanel.setMaximumSize(new Dimension(600, 50));
        buttonPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        createRoomButton = new JButton("➕ Crear Sala");
        createRoomButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        createRoomButton.addActionListener(e -> handleCreateRoom());

        joinRoomButton = new JButton("🤝 Unirse a Sala");
        joinRoomButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        joinRoomButton.addActionListener(e -> handleJoinRoom());

        refreshButton = new JButton("🔄 Actualizar");
        refreshButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        refreshButton.addActionListener(e -> requestRoomList());

        buttonPanel.add(createRoomButton);
        buttonPanel.add(joinRoomButton);
        buttonPanel.add(refreshButton);
        mainPanel.add(buttonPanel);

        add(mainPanel);
        controller.setCurrentView(this);
    }

    private void requestRoomList() {
        controller.sendMessage(Protocol.LIST_ROOMS);
    }

    private void handleCreateRoom() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Deseas crear una nueva sala?\nEsperarás a que otro jugador se una.",
            "Crear Sala",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.sendMessage(Protocol.CREATE_ROOM);
            createRoomButton.setEnabled(false);
            createRoomButton.setText("Creando...");
        }
    }

    private void handleJoinRoom() {
        int selectedRow = roomTable.getSelectedRow();

        if (selectedRow < 0) {
            JOptionPane.showMessageDialog(
                this,
                "Por favor selecciona una sala de la tabla",
                "No hay sala seleccionada",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        String roomId = (String) tableModel.getValueAt(selectedRow, 0);
        String estado = (String) tableModel.getValueAt(selectedRow, 3);

        if (!"Esperando".equals(estado)) {
            JOptionPane.showMessageDialog(
                this,
                "Esta sala ya está llena o en juego",
                "Sala no disponible",
                JOptionPane.WARNING_MESSAGE
            );
            return;
        }

        controller.setCurrentRoomId(roomId);
        controller.sendMessage(Protocol.JOIN_ROOM + ":" + roomId);
        joinRoomButton.setEnabled(false);
        joinRoomButton.setText("Uniéndose...");
    }

    private void handleLogout() {
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "¿Seguro que quieres cerrar sesión?",
            "Cerrar Sesión",
            JOptionPane.YES_NO_OPTION
        );

        if (confirm == JOptionPane.YES_OPTION) {
            controller.logout();
            dispose();
        }
    }

    public void updateRoomList(String roomData) {
        SwingUtilities.invokeLater(() -> {
            tableModel.setRowCount(0);

            if (roomData == null || roomData.trim().isEmpty()) {
                return;
            }

            String[] rooms = roomData.split("\\|");
            for (String roomStr : rooms) {
                String[] fields = roomStr.split(",");
                if (fields.length >= 5) {
                    String roomId = fields[0];
                    String player1 = fields[1];
                    String player2 = fields[2];
                    String state = fields[3];
                    String playerCount = fields[4];

                    String translatedState = state;
                    switch (state) {
                        case "WAITING": translatedState = "Esperando"; break;
                        case "PLACING_SHIPS": translatedState = "Colocando Barcos"; break;
                        case "PLAYING": translatedState = "Jugando"; break;
                        case "FINISHED": translatedState = "Terminado"; break;
                    }

                    tableModel.addRow(new Object[]{roomId, player1, player2, translatedState, playerCount});
                }
            }

            createRoomButton.setEnabled(true);
            createRoomButton.setText("➕ Crear Sala");
            joinRoomButton.setEnabled(true);
            joinRoomButton.setText("🤝 Unirse a Sala");
        });
    }

    private double calcularRatio(int victorias, int derrotas) {
        int total = victorias + derrotas;
        if (total == 0) return 0.0;
        return (victorias * 100.0) / total;
    }

    public void showError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, message, "Error", JOptionPane.ERROR_MESSAGE);
            createRoomButton.setEnabled(true);
            createRoomButton.setText("➕ Crear Sala");
            joinRoomButton.setEnabled(true);
            joinRoomButton.setText("🤝 Unirse a Sala");
        });
    }

    public void onRoomCreated(String roomId) {
        SwingUtilities.invokeLater(() -> {
            System.out.println("[LobbyGUI] Sala creada: " + roomId + ". Abriendo RoomWaitingGUI...");

            // Cerrar el LobbyGUI y abrir la RoomWaitingGUI
            dispose();
            RoomWaitingGUI waitingGUI = new RoomWaitingGUI(playerName, roomId);
            waitingGUI.setVisible(true);

            System.out.println("[LobbyGUI] RoomWaitingGUI abierta correctamente");
        });
    }

    public void onAutoJoined(String roomId) {
        SwingUtilities.invokeLater(() -> {
            String currentStats = playerStatsLabel.getText();
            String newStats = currentStats + " | Sala: " + roomId;
            playerStatsLabel.setText(newStats);
            createRoomButton.setEnabled(false);
            createRoomButton.setText("En Sala");
        });
    }

    public void onJoinedRoom() {
        SwingUtilities.invokeLater(() -> {
            // Obtener el roomId del controlador
            String roomId = controller.getCurrentRoomId();

            // Cerrar el LobbyGUI y abrir la RoomWaitingGUI
            dispose();
            RoomWaitingGUI waitingGUI = new RoomWaitingGUI(playerName, roomId);
            waitingGUI.setVisible(true);
        });
    }

    public void onPlayerJoined(String playerName) {
        SwingUtilities.invokeLater(() -> {
            requestRoomList();
            String currentStats = playerStatsLabel.getText();
            String newStats = currentStats + " | " + playerName;
            playerStatsLabel.setText(newStats);
        });
    }
}
