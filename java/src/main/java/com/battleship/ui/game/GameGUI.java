package com.battleship.ui.game;

import com.battleship.client.ClientController;
import com.battleship.common.Protocol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GameGUI extends JFrame {

    private ClientController controller;
    private String playerName;
    private String opponentName;

    // Estado del tablero del oponente
    // 0 = No disparado, 1 = MISS, 2 = HIT, 3 = SHIP_SUNK
    private int[][] enemyBoard = new int[8][8];
    private boolean isMyTurn = false;

    // Barcos hundidos del oponente (para mostrarlos cuando se hundan)
    private List<SunkShip> sunkShips = new ArrayList<>();

    // Componentes UI
    private JLabel[][] boardCells;
    private JLabel turnLabel;
    private JLabel opponentShipsLabel;
    private JLabel lastShotLabel;

    // Colores
    private static final Color COLOR_EMPTY = new Color(255, 255, 255);      // Blanco
    private static final Color COLOR_MISS = new Color(33, 150, 243);        // Azul (agua)
    private static final Color COLOR_HIT = new Color(244, 67, 54);          // Rojo (tocado)
    private static final Color COLOR_SUNK = new Color(76, 175, 80);        // Verde (hundido)
    private static final Color COLOR_GRID = new Color(224, 224, 224);       // Gris
    private static final Color COLOR_TURN_YOURS = new Color(76, 175, 80);   // Verde
    private static final Color COLOR_TURN_OPPONENT = new Color(244, 67, 54); // Rojo

    public GameGUI(String playerName, String opponentName) {
        this.playerName = playerName;
        this.opponentName = opponentName;
        this.controller = ClientController.getInstance();

        initComponents();
        controller.setCurrentView(this);

        // Inicialmente no es tu turno
        updateTurnIndicator();
    }

    private void initComponents() {
        setTitle("Batalla Naval - " + playerName + " vs " + opponentName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));

        // Panel del tablero (centro)
        JPanel boardPanel = createBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Panel de información (derecha)
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Tablero Enemigo (8x8)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(13, 71, 161));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel hintLabel = new JLabel("<html><center>Haz clic en una casilla para disparar.<br>Solo puedes disparar en tu turno.</center></html>",
            SwingConstants.CENTER);
        hintLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        hintLabel.setForeground(new Color(117, 117, 117));
        panel.add(hintLabel, BorderLayout.SOUTH);

        JPanel boardGrid = new JPanel(new GridLayout(8, 8, 1, 1));
        boardGrid.setBackground(COLOR_GRID);
        boardCells = new JLabel[8][8];

        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                JLabel cell = createBoardCell(row, col);
                boardCells[row][col] = cell;
                boardGrid.add(cell);
            }
        }

        JScrollPane scrollPane = new JScrollPane(boardGrid);
        scrollPane.setPreferredSize(new Dimension(350, 350));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createBoardCell(int row, int col) {
        JLabel cell = new JLabel("", SwingConstants.CENTER);
        cell.setPreferredSize(new Dimension(32, 32));
        cell.setOpaque(true);
        cell.setBackground(COLOR_EMPTY);
        cell.setBorder(BorderFactory.createLineBorder(COLOR_GRID, 1));
        cell.setFont(new Font("Arial", Font.BOLD, 16));

        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("[GameGUI] mouseClicked en [" + row + "][" + col + "]");
                handleCellClick(row, col);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (enemyBoard[row][col] == 0 && isMyTurn) {
                    cell.setBackground(new Color(179, 229, 252)); // Hover azul claro
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                refreshCell(row, col);
            }
        });

        return cell;
    }

    private JPanel createInfoPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(250, 600));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Información", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(13, 71, 161));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoContainer = new JPanel();
        infoContainer.setLayout(new BoxLayout(infoContainer, BoxLayout.Y_AXIS));
        infoContainer.setBackground(new Color(255, 255, 255));

        turnLabel = createInfoLabel("Turno: Esperando...");
        opponentShipsLabel = createInfoLabel("Barcos enemigos hundidos: 0/5");
        lastShotLabel = createInfoLabel("Último disparo: -");

        infoContainer.add(turnLabel);
        infoContainer.add(Box.createVerticalStrut(15));
        infoContainer.add(opponentShipsLabel);
        infoContainer.add(Box.createVerticalStrut(15));
        infoContainer.add(lastShotLabel);
        infoContainer.add(Box.createVerticalStrut(30));

        // Leyenda
        JPanel legendPanel = createLegendPanel();
        infoContainer.add(legendPanel);

        panel.add(infoContainer, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createLegendPanel() {
        JPanel panel = new JPanel(new GridLayout(4, 1, 5, 5));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189)),
            "Leyenda"
        ));

        panel.add(createLegendItem("No disparado", COLOR_EMPTY));
        panel.add(createLegendItem("Agua (MISS)", COLOR_MISS));
        panel.add(createLegendItem("Tocado (HIT)", COLOR_HIT));
        panel.add(createLegendItem("Hundido", COLOR_SUNK));

        return panel;
    }

    private JPanel createLegendItem(String text, Color color) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 2));
        panel.setBackground(new Color(255, 255, 255));

        JPanel colorBox = new JPanel();
        colorBox.setPreferredSize(new Dimension(20, 20));
        colorBox.setBackground(color);
        colorBox.setBorder(BorderFactory.createLineBorder(COLOR_GRID, 1));

        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", Font.PLAIN, 10));

        panel.add(colorBox);
        panel.add(label);

        return panel;
    }

    private JLabel createInfoLabel(String text) {
        JLabel label = new JLabel(text, SwingConstants.LEFT);
        label.setFont(new Font("Arial", Font.PLAIN, 12));
        label.setForeground(new Color(66, 66, 66));
        label.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        label.setBackground(new Color(245, 245, 245));
        label.setOpaque(true);
        return label;
    }

    private void handleCellClick(int row, int col) {
        System.out.println("[GameGUI] handleCellClick llamado - row=" + row + ", col=" + col + ", isMyTurn=" + isMyTurn);
        System.out.println("[GameGUI] enemyBoard[" + row + "][" + col + "] = " + enemyBoard[row][col]);

        if (!isMyTurn) {
            System.out.println("[GameGUI] NO es tu turno, mostrando alerta");
            JOptionPane.showMessageDialog(this,
                "¡No es tu turno! Espera a que el oponente juegue.",
                "Turno del Oponente",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (enemyBoard[row][col] != 0) {
            System.out.println("[GameGUI] Casilla ya disparada, mostrando alerta");
            // Ya se disparó aquí - mostrar mensaje y no hacer nada
            JOptionPane.showMessageDialog(this,
                "Ya disparaste a esta casilla. Elige otra.",
                "Casilla Ya Disparada",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        System.out.println("[GameGUI] Enviando SHOOT:" + row + ":" + col);
        // Enviar disparo al servidor: SHOOT:row:col
        controller.sendMessage(Protocol.SHOOT + ":" + row + ":" + col);

        // NO deshabilitar el turno aquí - esperamos la respuesta del servidor
    }

    private void refreshCell(int row, int col) {
        JLabel cell = boardCells[row][col];
        int state = enemyBoard[row][col];

        switch (state) {
            case 0: // No disparado
                cell.setBackground(COLOR_EMPTY);
                cell.setText("");
                break;
            case 1: // MISS
                cell.setBackground(COLOR_MISS);
                cell.setText("○");
                cell.setForeground(Color.WHITE);
                break;
            case 2: // HIT
                cell.setBackground(COLOR_HIT);
                cell.setText("X");
                cell.setForeground(Color.WHITE);
                break;
            case 3: // SHIP_SUNK - Pintar directamente en verde aquí
                cell.setBackground(COLOR_SUNK);
                cell.setText("■");
                cell.setForeground(Color.WHITE);
                break;
        }

        cell.repaint();
    }

    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                refreshCell(row, col);
            }
        }
        // Actualizar barcos hundidos
        refreshSunkShips();
    }

    private void refreshSunkShips() {
        // Limpiar representación previa de barcos hundidos
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                if (enemyBoard[row][col] == 3) {
                    JLabel cell = boardCells[row][col];
                    cell.setBackground(COLOR_SUNK);
                    cell.setText("■");
                    cell.setForeground(Color.WHITE);
                }
            }
        }
    }

    private void updateTurnIndicator() {
        SwingUtilities.invokeLater(() -> {
            if (isMyTurn) {
                turnLabel.setText("Turno: ¡ES TU TURNO!");
                turnLabel.setBackground(new Color(200, 230, 201));
                turnLabel.setForeground(new Color(0, 100, 0));
            } else {
                turnLabel.setText("Turno: Oponente...");
                turnLabel.setBackground(new Color(255, 205, 210));
                turnLabel.setForeground(new Color(150, 0, 0));
            }
        });
    }

    // --- Métodos públicos llamados desde ClientController ---

    public void onYourTurn() {
        SwingUtilities.invokeLater(() -> {
            isMyTurn = true;
            updateTurnIndicator();
        });
    }

    public void onOpponentTurn() {
        SwingUtilities.invokeLater(() -> {
            isMyTurn = false;
            updateTurnIndicator();
        });
    }

    public void onShotResult(String result, int row, int col) {
        System.out.println("[GameGUI] onShotResult llamado - result=" + result + ", row=" + row + ", col=" + col);
        SwingUtilities.invokeLater(() -> {
            System.out.println("[GameGUI] onShotResult - en SwingUtilities.invokeLater");

            // IMPORTANTE: No sobrescribir si ya está marcada como SUNK (3)
            // Esto puede pasar si onShipSunk se ejecutó antes
            int currentState = enemyBoard[row][col];
            if (currentState == 3) {
                System.out.println("[GameGUI] Casilla [" + row + "][" + col + "] ya es SUNK (3), NO sobrescribir");
                return; // No hacer nada, ya está pintada en verde
            }

            int state = result.equals("HIT") ? 2 : 1;
            enemyBoard[row][col] = state;
            System.out.println("[GameGUI] enemyBoard[" + row + "][" + col + "] = " + state);

            // Actualizar etiqueta de último disparo
            String resultText = result.equals("HIT") ? "¡Tocado!" : "Agua";
            lastShotLabel.setText("Último disparo: (" + row + "," + col + ") - " + resultText);

            refreshCell(row, col);

            // Si es MISS, el turno cambia
            if (result.equals("MISS")) {
                isMyTurn = false;
                updateTurnIndicator();
            }
            System.out.println("[GameGUI] onShotResult completado");
        });
    }

    public void onOpponentShot(String result, int row, int col) {
        SwingUtilities.invokeLater(() -> {
            // El oponente disparó en tu tablero (no lo mostramos aquí porque no vemos tu tablero)
            // Solo actualizar turno
            String resultText = result.equals("HIT") ? "Tocado" : "Agua";
            lastShotLabel.setText("Disparo enemigo: (" + row + "," + col + ") - " + resultText);

            // Si el oponente falló, ahora es tu turno
            if (result.equals("MISS")) {
                isMyTurn = true;
                updateTurnIndicator();
            } else {
                isMyTurn = false;
                updateTurnIndicator();
            }
        });
    }

    public void onShipSunk(int size, int x, int y, String orientation) {
        System.out.println("[GameGUI] onShipSunk llamado - size=" + size + ", x=" + x + ", y=" + y + ", orient=" + orientation);
        SwingUtilities.invokeLater(() -> {
            System.out.println("[GameGUI] onShipSunk - en SwingUtilities.invokeLater");
            boolean horizontal = orientation.equals("H");
            System.out.println("[GameGUI] Barco " + (horizontal ? "HORIZONTAL" : "VERTICAL"));

            // Agregar a la lista de barcos hundidos
            sunkShips.add(new SunkShip(size, x, y, horizontal));

            // PRIMERO: Actualizar enemyBoard para todas las celdas del barco
            System.out.println("[GameGUI] Actualizando enemyBoard para " + size + " casillas:");
            for (int i = 0; i < size; i++) {
                int cellX = horizontal ? x + i : x;
                int cellY = horizontal ? y : y + i;
                int oldState = enemyBoard[cellY][cellX];
                enemyBoard[cellY][cellX] = 3; // Marcar como hundido
                System.out.println("[GameGUI]   [" + cellY + "][" + cellX + "]: " + oldState + " -> 3 (SUNK)");
            }

            // SEGUNDO: Pintar directamente todas las celdas del barco en verde
            System.out.println("[GameGUI] Pintando celdas en verde:");
            for (int i = 0; i < size; i++) {
                int cellX = horizontal ? x + i : x;
                int cellY = horizontal ? y : y + i;
                JLabel cell = boardCells[cellY][cellX];
                System.out.println("[GameGUI]   [" + cellY + "][" + cellX + "] - setForeground, setText, repaint");
                cell.setBackground(COLOR_SUNK);
                cell.setText("■");
                cell.setForeground(Color.WHITE);
                cell.repaint();
            }

            // Actualizar contador
            opponentShipsLabel.setText("Barcos enemigos hundidos: " + sunkShips.size() + "/5");

            // Refrescar todo el tablero ANTES del JOptionPane
            refreshBoard();

            System.out.println("[GameGUI] Mostrando JOptionPane...");

            // Mostrar mensaje AL FINAL
            String orientText = horizontal ? "Horizontal" : "Vertical";
            JOptionPane.showMessageDialog(this,
                "¡Has hundido un barco enemigo!\n\n" +
                "Tamaño: " + size + " casillas\n" +
                "Posición: (" + x + ", " + y + ")\n" +
                "Orientación: " + orientText,
                "¡Barco Hundido!",
                JOptionPane.INFORMATION_MESSAGE);

            System.out.println("[GameGUI] JOptionPane cerrado");
            System.out.println("[GameGUI] onShipSunk completado");
        });
    }

    public void onYourShipSunk(int size, int x, int y, String orientation) {
        SwingUtilities.invokeLater(() -> {
            // Mostrar mensaje diferente al jugador cuyo barco fue hundido
            String orientText = orientation.equals("H") ? "Horizontal" : "Vertical";
            JOptionPane.showMessageDialog(this,
                "¡Te han hundido un barco!\n\n" +
                "Tamaño: " + size + " casillas\n" +
                "Posición: (" + x + ", " + y + ")\n" +
                "Orientación: " + orientText + "\n\n" +
                "¡Cuidado! Quedan menos barcos en tu flota.",
                "¡Barco Hundido!",
                JOptionPane.WARNING_MESSAGE);
        });
    }

    public void onGameOver(boolean won) {
        SwingUtilities.invokeLater(() -> {
            String message = won
                ? "¡FELICIDADES! Has ganado la partida.\n\nTodos los barcos enemigos han sido hundidos."
                : "Has perdido la partida.\n\nEl oponente ha hundido todos tus barcos.";

            String title = won ? "¡Victoria!" : "Derrota";

            JOptionPane.showMessageDialog(this, message, title,
                won ? JOptionPane.INFORMATION_MESSAGE : JOptionPane.ERROR_MESSAGE);

            // Cerrar ventana y volver al lobby
            dispose();

            // TODO: Crear y abrir LobbyGUI nuevamente
            // Por ahora, simplemente cerramos
        });
    }

    public void onError(String errorMessage) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this,
                "Error: " + errorMessage,
                "Error del Servidor",
                JOptionPane.ERROR_MESSAGE);

            // Si el error fue por disparar a una casilla inválida, mantener el turno
            // No cambiamos isMyTurn porque el servidor no cambió el turno
        });
    }

    // Clase interna para representar barcos hundidos
    private static class SunkShip {
        int size;
        int x;
        int y;
        boolean horizontal;

        SunkShip(int size, int x, int y, boolean horizontal) {
            this.size = size;
            this.x = x;
            this.y = y;
            this.horizontal = horizontal;
        }
    }
}
