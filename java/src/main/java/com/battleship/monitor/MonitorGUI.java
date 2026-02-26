package com.battleship.monitor;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Interfaz gráfica del Monitor para visualizar salas y estadísticas en tiempo real.
 */
public class MonitorGUI extends JFrame implements MonitorClient.MonitorDataListener {

    private MonitorClient client;
    private DefaultTableModel tableModel;
    private JTable roomTable;
    private JLabel statusLabel;

    // Panel de estadísticas
    private JTextArea statsArea;

    // Datos de las salas parseados
    private java.util.List<RoomData> rooms = new java.util.ArrayList<>();

    public MonitorGUI() {
        initComponents();
        client = new MonitorClient(this);

        // Conectar automáticamente al servidor después de construir la GUI
        SwingUtilities.invokeLater(this::connectToServer);
    }

    private void initComponents() {
        setTitle("Monitor de Batalla Naval - ADMIN");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);

        // Panel superior: estado de conexión
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 10));
        topPanel.setBackground(new Color(248, 249, 250));
        topPanel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(206, 212, 218)));

        JLabel titleLabel = new JLabel("Monitor de Salas");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));

        statusLabel = new JLabel("Conectando al servidor...");
        statusLabel.setForeground(new Color(255, 193, 7));
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 12));

        topPanel.add(titleLabel);
        topPanel.add(new JLabel(" | "));
        topPanel.add(statusLabel);

        // Tabla de salas
        String[] columnNames = {"ID Sala", "Jugador 1", "Jugador 2", "Estado", "Turno"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        roomTable = new JTable(tableModel);
        roomTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        roomTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                updateStatsPanel();
            }
        });

        JScrollPane tableScrollPane = new JScrollPane(roomTable);
        tableScrollPane.setBorder(BorderFactory.createTitledBorder("Salas Activas (Máximo 4)"));

        // Panel de estadísticas
        statsArea = new JTextArea();
        statsArea.setEditable(false);
        statsArea.setFont(new Font("Monospaced", Font.PLAIN, 12));
        JScrollPane statsScrollPane = new JScrollPane(statsArea);
        statsScrollPane.setBorder(BorderFactory.createTitledBorder("Estadísticas de Partida"));
        statsScrollPane.setPreferredSize(new Dimension(300, 0));

        // Layout principal
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableScrollPane, statsScrollPane);
        splitPane.setResizeWeight(0.6);

        add(topPanel, BorderLayout.NORTH);
        add(splitPane, BorderLayout.CENTER);
    }

    private void connectToServer() {
        // Conectar automáticamente a localhost:9090
        String host = "localhost";
        int port = 9090;

        boolean connected = client.connect(host, port);

        if (connected) {
            statusLabel.setText("Conectado a " + host + ":" + port);
            statusLabel.setForeground(new Color(40, 167, 69));
        } else {
            statusLabel.setText("Error: No se pudo conectar al servidor");
            statusLabel.setForeground(Color.RED);
            JOptionPane.showMessageDialog(this,
                    "No se pudo conectar al servidor en " + host + ":" + port + "\n" +
                    "Verifique que el servidor esté corriendo.",
                    "Error de Conexión",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    @Override
    public void onDataReceived(String data) {
        // Parsear los datos y actualizar la tabla en el hilo de Swing
        SwingUtilities.invokeLater(() -> {
            parseAndUpdateTable(data);
        });
    }

    @Override
    public void onConnectionError(String error) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, error, "Error de Conexión", JOptionPane.ERROR_MESSAGE);
            statusLabel.setText("Error: " + error);
            statusLabel.setForeground(Color.RED);
        });
    }

    /**
     * Parsea el string de datos del servidor y actualiza la tabla.
     * Formato: ID|Jugador1|Jugador2|Estado|Turno|P1Shots|P1Hits|P1Sunk|P2Shots|P2Hits|P2Sunk;...
     */
    private void parseAndUpdateTable(String data) {
        rooms.clear();
        tableModel.setRowCount(0);

        if (data == null || data.trim().isEmpty()) {
            return;
        }

        String[] roomsData = data.split(";");
        for (String roomStr : roomsData) {
            String[] fields = roomStr.split("\\|");
            if (fields.length >= 11) {
                RoomData room = new RoomData();
                room.id = fields[0];
                room.player1 = fields[1];
                room.player2 = fields[2];
                room.state = fields[3];
                room.turn = fields[4];

                try {
                    room.p1Shots = Integer.parseInt(fields[5]);
                    room.p1Hits = Integer.parseInt(fields[6]);
                    room.p1Sunk = Integer.parseInt(fields[7]);
                    room.p2Shots = Integer.parseInt(fields[8]);
                    room.p2Hits = Integer.parseInt(fields[9]);
                    room.p2Sunk = Integer.parseInt(fields[10]);
                } catch (NumberFormatException e) {
                    System.err.println("Error parseando estadísticas: " + e.getMessage());
                    continue; // Saltar esta sala si hay error
                }

                rooms.add(room);

                // Agregar fila a la tabla
                tableModel.addRow(new Object[]{
                    room.id, room.player1, room.player2, room.state, room.turn
                });
            }
        }

        // Actualizar panel de estadísticas si hay una sala seleccionada
        updateStatsPanel();
    }

    /**
     * Actualiza el panel de estadísticas con la sala seleccionada
     */
    private void updateStatsPanel() {
        int selectedRow = roomTable.getSelectedRow();
        if (selectedRow < 0 || selectedRow >= rooms.size()) {
            statsArea.setText("Selecciona una sala para ver estadísticas");
            return;
        }

        RoomData room = rooms.get(selectedRow);

        StringBuilder sb = new StringBuilder();
        sb.append("=== Estadísticas de ").append(room.id).append(" ===\n\n");

        sb.append("JUGADOR 1: ").append(room.player1).append("\n");
        sb.append("  Disparos Totales: ").append(room.p1Shots).append("\n");
        sb.append("  Aciertos: ").append(room.p1Hits).append("\n");
        sb.append("  Barcos Hundidos: ").append(room.p1Sunk).append("/5\n");

        if (room.p1Shots > 0) {
            double accuracy = (room.p1Hits * 100.0) / room.p1Shots;
            sb.append("  Efectividad: ").append(String.format("%.1f%%", accuracy)).append("\n");
        }

        sb.append("\n");
        sb.append("JUGADOR 2: ").append(room.player2).append("\n");
        sb.append("  Disparos Totales: ").append(room.p2Shots).append("\n");
        sb.append("  Aciertos: ").append(room.p2Hits).append("\n");
        sb.append("  Barcos Hundidos: ").append(room.p2Sunk).append("/5\n");

        if (room.p2Shots > 0) {
            double accuracy = (room.p2Hits * 100.0) / room.p2Shots;
            sb.append("  Efectividad: ").append(String.format("%.1f%%", accuracy)).append("\n");
        }

        sb.append("\n");
        sb.append("Estado: ").append(room.state).append("\n");
        sb.append("Turno: ").append(room.turn).append("\n");

        // Determinar quién va ganando
        if (room.p1Sunk > room.p2Sunk) {
            sb.append("\nVentaja: ").append(room.player1).append(" (más barcos hundidos)");
        } else if (room.p2Sunk > room.p1Sunk) {
            sb.append("\nVentaja: ").append(room.player2).append(" (más barcos hundidos)");
        } else {
            sb.append("\nPartida empatada en barcos hundidos");
        }

        statsArea.setText(sb.toString());
    }

    /**
     * Clase interna para almacenar datos de una sala
     */
    private static class RoomData {
        String id;
        String player1;
        String player2;
        String state;
        String turn;
        int p1Shots;
        int p1Hits;
        int p1Sunk;
        int p2Shots;
        int p2Hits;
        int p2Sunk;
    }
}
