package com.battleship.ui.game;

import com.battleship.client.ClientController;
import com.battleship.model.Ship;
import com.battleship.common.Protocol;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ShipPlacementGUI extends JFrame {

    private ClientController controller;
    private String playerName;

    // Tamaños de barcos ajustados para tablero 8x8
    private final int[] SHIP_SIZES = {2, 2, 3, 3, 4};
    
    // Estado
    private Ship[] ships;
    private Ship selectedShip;
    private boolean opponentReady = false;
    private int placedShipsCount = 0;
    private int hoverRow = -1;
    private int hoverCol = -1;
    
    // Componentes UI
    private JPanel shipsPanel;
    private JLabel[][] boardCells;
    private JLabel statusLabel;
    private JLabel opponentStatusLabel;
    private JButton readyButton;
    
    // Constantes de colores
    private static final Color COLOR_VALID = new Color(76, 175, 80);      // Verde
    private static final Color COLOR_INVALID = new Color(244, 67, 54);    // Rojo
    private static final Color COLOR_SHIP = new Color(33, 150, 243);      // Azul
    private static final Color COLOR_EMPTY = new Color(255, 255, 255);   // Blanco
    private static final Color COLOR_HOVER = new Color(179, 229, 252);   // Azul claro
    private static final Color COLOR_GRID = new Color(224, 224, 224);     // Gris

    public ShipPlacementGUI(String playerName) {
        this.playerName = playerName;
        this.controller = ClientController.getInstance();

        initShips();
        initComponents();
        setupEventHandlers();

        controller.setCurrentView(this);
    }

    private void initShips() {
        ships = new Ship[SHIP_SIZES.length];
        for (int i = 0; i < SHIP_SIZES.length; i++) {
            ships[i] = new Ship(i, SHIP_SIZES[i]);
        }
    }

    private void initComponents() {
        setTitle("Batalla Naval - Colocación de Barcos: " + playerName);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(new Color(240, 245, 250));

        // Panel de barcos (izquierda)
        shipsPanel = createShipsPanel();
        mainPanel.add(shipsPanel, BorderLayout.WEST);

        // Panel del tablero (centro)
        JPanel boardPanel = createBoardPanel();
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        // Panel de información (derecha)
        JPanel infoPanel = createInfoPanel();
        mainPanel.add(infoPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private JPanel createShipsPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setPreferredSize(new Dimension(250, 600));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Tus Barcos", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(13, 71, 161));
        panel.add(titleLabel, BorderLayout.NORTH);

        JLabel hintLabel = new JLabel("<html><center>Clic en barco → Selecciona<br>Clic en tablero → Coloca<br>Presiona 'R' para rotar<br>Clic en barco colocado para eliminar</center></html>", SwingConstants.CENTER);
        hintLabel.setFont(new Font("Arial", Font.PLAIN, 10));
        hintLabel.setForeground(new Color(117, 117, 117));
        panel.add(hintLabel, BorderLayout.SOUTH);

        JPanel shipsContainer = new JPanel();
        shipsContainer.setLayout(new BoxLayout(shipsContainer, BoxLayout.Y_AXIS));
        shipsContainer.setBackground(new Color(255, 255, 255));

        for (Ship ship : ships) {
            JPanel shipPanel = createShipPanel(ship);
            shipsContainer.add(shipPanel);
            shipsContainer.add(Box.createVerticalStrut(10));
        }

        JScrollPane scrollPane = new JScrollPane(shipsContainer);
        scrollPane.setBorder(null);
        scrollPane.setBackground(new Color(255, 255, 255));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createShipPanel(Ship ship) {
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setMaximumSize(new Dimension(220, 70));

        // Panel para mostrar el tamaño visualmente
        JPanel shipVisualPanel = new JPanel();
        shipVisualPanel.setBackground(new Color(245, 245, 245));
        
        JLabel shipVisualLabel = new JLabel();
        shipVisualLabel.setFont(new Font("Arial", Font.BOLD, 16));
        shipVisualLabel.setForeground(COLOR_SHIP);
        
        // Mostrar cuadrados representando el tamaño del barco
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < Math.min(ship.getLength(), 8); i++) {
            sb.append("■ ");
        }
        if (ship.getLength() > 8) {
            sb.append("... ");
        }
        shipVisualLabel.setText(sb.toString());
        
        shipVisualPanel.add(shipVisualLabel);

        JLabel infoLabel = new JLabel(
            "<html><b>Barco " + (ship.getShipId() + 1) + "</b><br>Tamaño: " + ship.getLength() + " casillas<br>Orientación: " + (ship.isHorizontal() ? "Horizontal" : "Vertical") + "</html>",
            SwingConstants.CENTER
        );
        infoLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        infoLabel.setForeground(new Color(66, 66, 66));

        panel.add(shipVisualPanel, BorderLayout.NORTH);
        panel.add(infoLabel, BorderLayout.CENTER);

        // Clic para seleccionar barco
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                if (ship.isPlaced()) {
                    removeShipFromBoard(ship);
                } else {
                    selectedShip = ship;
                    updateShipPanelSelection();
                }
            }
        });

        return panel;
    }

    private JPanel createBoardPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(new Color(255, 255, 255));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(189, 189, 189), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        JLabel titleLabel = new JLabel("Tu Tablero (8x8)", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(13, 71, 161));
        panel.add(titleLabel, BorderLayout.NORTH);

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
        scrollPane.setPreferredSize(new Dimension(300, 300));
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JLabel createBoardCell(int row, int col) {
        JLabel cell = new JLabel("", SwingConstants.CENTER);
        cell.setPreferredSize(new Dimension(30, 30));
        cell.setOpaque(true);
        cell.setBackground(COLOR_EMPTY);
        cell.setBorder(BorderFactory.createLineBorder(COLOR_GRID, 1));
        cell.setName(row + "," + col); // Guardar coordenadas en el nombre

        cell.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                System.out.println("[ShipPlacementGUI] mouseClicked en [" + row + "][" + col + "]");
                handleBoardCellClick(row, col);
            }

            @Override
            public void mouseEntered(MouseEvent e) {
                if (selectedShip != null && !selectedShip.isPlaced()) {
                    hoverRow = row;
                    hoverCol = col;
                    showShipPreview(row, col);
                }
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hoverRow = -1;
                hoverCol = -1;
                clearPreview();
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

        JLabel titleLabel = new JLabel("Estado", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        titleLabel.setForeground(new Color(13, 71, 161));
        panel.add(titleLabel, BorderLayout.NORTH);

        JPanel infoContainer = new JPanel();
        infoContainer.setLayout(new BoxLayout(infoContainer, BoxLayout.Y_AXIS));
        infoContainer.setBackground(new Color(255, 255, 255));

        statusLabel = createInfoLabel("Barcos colocados: 0/5");
        opponentStatusLabel = createInfoLabel("Oponente: Esperando...");

        infoContainer.add(statusLabel);
        infoContainer.add(Box.createVerticalStrut(15));
        infoContainer.add(opponentStatusLabel);
        infoContainer.add(Box.createVerticalStrut(30));

        readyButton = new JButton("¡LISTO!");
        readyButton.setFont(new Font("Arial", Font.BOLD, 14));
        readyButton.setPreferredSize(new Dimension(180, 45));
        readyButton.setBackground(new Color(76, 175, 80));
        readyButton.setForeground(Color.WHITE);
        readyButton.setFocusPainted(false);
        readyButton.setBorderPainted(false);
        readyButton.setEnabled(false);
        readyButton.addActionListener(e -> handleReadyButton());

        infoContainer.add(readyButton);

        panel.add(infoContainer, BorderLayout.CENTER);

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

    private void setupEventHandlers() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_R) {
                    if (selectedShip != null && !selectedShip.isPlaced()) {
                        selectedShip.rotate();
                        updateShipPanelSelection();
                    }
                }
            }
        });
        setFocusable(true);
    }

    private void placeShip(Ship ship, int row, int col) {
        if (isValidPlacement(ship, row, col)) {
            ship.placeAt(col, row);
            placeShipOnBoard(ship);
            placedShipsCount++;
            updateStatus();
        } else {
            JOptionPane.showMessageDialog(this, 
                "Posición inválida. El barco se sale del tablero o se solapa con otro.", 
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void removeShipFromBoard(Ship ship) {
        clearShipFromBoard(ship);
        ship.remove();
        placedShipsCount--;
        updateStatus();
        updateShipPanelSelection();
    }

    private void handleBoardCellClick(int row, int col) {
        // Primero, verificar si hay un barco en esta posición (para eliminar)
        for (Ship ship : ships) {
            if (ship.isPlaced()) {
                if (ship.isHorizontal()) {
                    int startX = ship.getStartX();
                    int endX = startX + ship.getLength() - 1;
                    int startY = ship.getStartY();
                    if (startY == row && col >= startX && col <= endX) {
                        removeShipFromBoard(ship);
                        return;
                    }
                } else {
                    int startY = ship.getStartY();
                    int endY = startY + ship.getLength() - 1;
                    int startX = ship.getStartX();
                    if (startX == col && row >= startY && row <= endY) {
                        removeShipFromBoard(ship);
                        return;
                    }
                }
            }
        }
        
        // Si hay un barco seleccionado, colocarlo en esta posición
        if (selectedShip != null && !selectedShip.isPlaced()) {
            placeShip(selectedShip, row, col);
        }
    }

    private boolean isValidPlacement(Ship ship, int row, int col) {
        int length = ship.getLength();
        boolean horizontal = ship.isHorizontal();

        // Verificar límites
        if (horizontal) {
            if (col + length > 8) return false;
        } else {
            if (row + length > 8) return false;
        }

        // Verificar solapamiento con otros barcos
        for (Ship other : ships) {
            if (other.isPlaced() && other != ship) {
                if (shipsOverlap(ship, col, row, other)) {
                    return false;
                }
            }
        }

        return true;
    }

    private boolean shipsOverlap(Ship ship1, int x1, int y1, Ship ship2) {
        int x2 = ship2.getStartX();
        int y2 = ship2.getStartY();
        int len1 = ship1.getLength();
        int len2 = ship2.getLength();

        boolean h1 = ship1.isHorizontal();
        boolean h2 = ship2.isHorizontal();

        if (h1 && h2) {
            // Ambos horizontales
            return y1 == y2 && ((x1 <= x2 && x2 < x1 + len1) || (x2 <= x1 && x1 < x2 + len2));
        } else if (!h1 && !h2) {
            // Ambos verticales
            return x1 == x2 && ((y1 <= y2 && y2 < y1 + len1) || (y2 <= y1 && y1 < y2 + len2));
        } else {
            // Uno horizontal, uno vertical - verificar intersección en cruce
            Ship horizontal = h1 ? ship1 : ship2;
            Ship vertical = h1 ? ship2 : ship1;
            
            int hx = horizontal.getStartX();
            int hy = horizontal.getStartY();
            int hlen = horizontal.getLength();
            
            int vx = vertical.getStartX();
            int vy = vertical.getStartY();
            int vlen = vertical.getLength();
            
            return vx >= hx && vx < hx + hlen && hy >= vy && hy < vy + vlen;
        }
    }

    private void placeShipOnBoard(Ship ship) {
        int startX = ship.getStartX();
        int startY = ship.getStartY();
        int length = ship.getLength();
        boolean horizontal = ship.isHorizontal();

        for (int i = 0; i < length; i++) {
            if (horizontal) {
                boardCells[startY][startX + i].setBackground(COLOR_SHIP);
            } else {
                boardCells[startY + i][startX].setBackground(COLOR_SHIP);
            }
        }
    }

    private void clearShipFromBoard(Ship ship) {
        int startX = ship.getStartX();
        int startY = ship.getStartY();
        int length = ship.getLength();
        boolean horizontal = ship.isHorizontal();

        for (int i = 0; i < length; i++) {
            if (horizontal) {
                boardCells[startY][startX + i].setBackground(COLOR_EMPTY);
            } else {
                boardCells[startY + i][startX].setBackground(COLOR_EMPTY);
            }
        }
    }

    private void showShipPreview(int row, int col) {
        if (isValidPlacement(selectedShip, row, col)) {
            highlightShip(selectedShip, row, col, COLOR_VALID);
        } else {
            highlightShip(selectedShip, row, col, COLOR_INVALID);
        }
    }

    private void highlightShip(Ship ship, int row, int col, Color color) {
        int length = ship.getLength();
        boolean horizontal = ship.isHorizontal();

        for (int i = 0; i < length; i++) {
            if (horizontal) {
                if (col + i < 16) {
                    boardCells[row][col + i].setBackground(color);
                }
            } else {
                if (row + i < 16) {
                    boardCells[row + i][col].setBackground(color);
                }
            }
        }
    }

    private void clearPreview() {
        // Restaurar colores originales
        refreshBoard();
    }

    private void refreshBoard() {
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                boardCells[row][col].setBackground(COLOR_EMPTY);
            }
        }

        // Re-colocar todos los barcos
        for (Ship ship : ships) {
            if (ship.isPlaced()) {
                placeShipOnBoard(ship);
            }
        }
    }

    private void updateShipPanelSelection() {
        // Actualizar el panel seleccionado visualmente
        // Recorrer todos los paneles de la lista de barcos
        // Nota: Implementación simplificada - podríamos mantener referencias a los paneles
        // Por ahora, solo actualizamos la UI cuando se selecciona/deselecciona
    }

    private void updateStatus() {
        SwingUtilities.invokeLater(() -> {
            statusLabel.setText("Barcos colocados: " + placedShipsCount + "/5");
            readyButton.setEnabled(placedShipsCount == 5);
        });
    }

    private void handleReadyButton() {
        // Validar que todos los barcos estén colocados
        if (placedShipsCount != 5) {
            JOptionPane.showMessageDialog(this, "Debes colocar los 5 barcos antes de continuar.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Construir el string de protocolo
        StringBuilder sb = new StringBuilder();
        for (Ship ship : ships) {
            if (ship.isPlaced()) {
                if (sb.length() > 0) sb.append(";");
                sb.append(ship.toProtocolString());
            }
        }

        String shipData = sb.toString();
        System.out.println("[ShipPlacementGUI] Enviando PLACE_SHIPS: " + shipData);
        controller.sendMessage(Protocol.PLACE_SHIPS + ":" + shipData);

        readyButton.setEnabled(false);
        readyButton.setText("Enviando...");
    }

    public void onShipsPlacedOK() {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "¡Tus barcos están listos!\nEsperando a tu oponente...", 
                "Barcos Listos", JOptionPane.INFORMATION_MESSAGE);
            readyButton.setText("¡Listo!");
            readyButton.setEnabled(false);
            readyButton.setBackground(new Color(158, 158, 158));
        });
    }

    public void onError(String message) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "Error: " + message, 
                "Error", JOptionPane.ERROR_MESSAGE);
            readyButton.setEnabled(true);
            readyButton.setText("¡LISTO!");
        });
    }

    public void onOpponentReady() {
        SwingUtilities.invokeLater(() -> {
            opponentStatusLabel.setText("Oponente: ¡Listo!");
            opponentStatusLabel.setForeground(new Color(76, 175, 80));
        });
    }

    public void onGameStart(String opponentName) {
        SwingUtilities.invokeLater(() -> {
            JOptionPane.showMessageDialog(this, 
                "¡El juego comienza!\n\nTe enfrentas a: " + opponentName, 
                "¡Juego Iniciado!", JOptionPane.INFORMATION_MESSAGE);
            
            // Cerrar esta ventana y abrir GameGUI (por implementar)
            dispose();
            
            // Por ahora, volvemos al RoomWaitingGUI para mantener el flujo
            // TODO: Implementar GameGUI cuando esté lista
        });
    }
}
