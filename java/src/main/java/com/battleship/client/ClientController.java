package com.battleship.client;

import com.battleship.common.Protocol;
import com.battleship.ui.lobby.LobbyGUI;
import com.battleship.ui.lobby.RoomWaitingGUI;
import com.battleship.ui.login.LoginGUI;
import com.battleship.ui.login.RegisterGUI;
import com.battleship.ui.game.ShipPlacementGUI;
import com.battleship.ui.game.GameGUI;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientController {

    // Instancia única (Singleton)
    private static ClientController instance;

    // Variables de Red
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Estado del Cliente
    private String playerName;
    private int victorias;
    private int derrotas;
    private String currentRoomId;
    private JFrame currentView; // Referencia a la ventana actual para poder cerrarla o mostrar alertas
    private boolean running = false; // Flag para controlar el hilo de escucha

    // Constructor privado para evitar 'new ClientController()'
    private ClientController() {
    }

    public static synchronized ClientController getInstance() {
        if (instance == null) {
            instance = new ClientController();
        }
        return instance;
    }

    /**
     * Intenta conectar al servidor. Retorna true si tuvo éxito, false si falló.
     */
    public boolean connect(String ip, int port) {
        // Si ya estamos conectados, no reconectar
        if (socket != null && !socket.isClosed() && socket.isConnected()) {
            return true;
        }

        try {
            socket = new Socket(ip, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Iniciar un hilo separado para escuchar al servidor sin congelar la interfaz
            running = true;
            new Thread(this::listenToServer).start();
            return true;
        } catch (IOException e) {
            System.err.println("Error al conectar: " + e.getMessage());
            return false;
        }
    }

    /**
     * Envía un mensaje al servidor.
     * @param msg
     */
    public void sendMessage(String msg) {
        if (out != null) {
            System.out.println("Enviando: " + msg); // Log para depuración
            out.println(msg);
        }
    }

    /**
     * Bucle infinito que escucha lo que dice el servidor. Se ejecuta en un hilo
     * secundario.
     */
    private void listenToServer() {
        try {
            String message;
            while (running && (message = in.readLine()) != null) {
                processMessage(message);
            }
        } catch (IOException e) {
            if (running) {
                System.out.println("Desconectado del servidor.");
                SwingUtilities.invokeLater(()
                        -> JOptionPane.showMessageDialog(currentView, "Conexión perdida con el servidor.", "Error", JOptionPane.ERROR_MESSAGE)
                );
            }
        }
    }

    /**
     * Procesa la respuesta del servidor y actualiza la GUI.
     */
    private void processMessage(String message) {
        System.out.println("Servidor dice: " + message);
        String[] parts = message.split(":", -1); // Sin limite para manejar todos los datos
        String command = parts[0];

        // Usamos invokeLater porque Swing no permite tocar la UI desde otro hilo
        SwingUtilities.invokeLater(() -> {
            switch (command) {
                case Protocol.LOGIN_OK -> handleLoginSuccess(parts);

                case Protocol.REGISTER_OK -> handleRegisterSuccess();

                case Protocol.LOGOUT_OK -> handleLogoutSuccess();

                case Protocol.ERROR -> {
                    String errorMsg = (parts.length > 1) ? parts[1] : "Error desconocido";
                    // Manejar errores específicos de REGISTER y LOGIN
                    if (currentView instanceof RegisterGUI) {
                        ((RegisterGUI) currentView).showRegistrationError(errorMsg);
                    } else if (currentView instanceof LoginGUI) {
                        ((LoginGUI) currentView).showServerError(errorMsg);
                    } else if (currentView instanceof LobbyGUI) {
                        ((LobbyGUI) currentView).showError(errorMsg);
                    } else if (currentView instanceof ShipPlacementGUI) {
                        ((ShipPlacementGUI) currentView).onError(errorMsg);
                    } else if (currentView instanceof GameGUI) {
                        ((GameGUI) currentView).onError(errorMsg);
                    } else {
                        JOptionPane.showMessageDialog(currentView, errorMsg, "Error del Servidor", JOptionPane.ERROR_MESSAGE);
                    }
                }

                case Protocol.ROOM_LIST -> {
                    if (currentView instanceof LobbyGUI && parts.length > 1) {
                        ((LobbyGUI) currentView).updateRoomList(parts[1]);
                    }
                }

                case Protocol.ROOM_CREATED -> {
                    if (currentView instanceof LobbyGUI && parts.length > 1) {
                        ((LobbyGUI) currentView).onRoomCreated(parts[1]);
                    }
                }

                case "AUTO_JOINED" -> {
                    if (currentView instanceof LobbyGUI && parts.length > 1) {
                        String roomId = parts[1];
                        currentRoomId = roomId;
                        
                        // Cerrar LobbyGUI y abrir RoomWaitingGUI
                        if (currentView != null) {
                            currentView.dispose();
                        }
                        
                        RoomWaitingGUI roomGUI = new RoomWaitingGUI(playerName, roomId);
                        roomGUI.setVisible(true);
                        setCurrentView(roomGUI);
                    }
                }

                case "JOINED_OK" -> {
                    if (currentView instanceof LobbyGUI && currentRoomId != null) {
                        // Esperar a recibir ROOM_INFO antes de abrir RoomWaitingGUI
                        // ROOM_INFO llegará después de JOINED_OK
                    }
                }

                case "ROOM_INFO" -> {
                    if (currentView instanceof LobbyGUI && parts.length > 2) {
                        String roomId = parts[1];
                        String player1Name = parts[2];
                        
                        // Cerrar LobbyGUI y abrir RoomWaitingGUI
                        if (currentView != null) {
                            currentView.dispose();
                        }
                        
                        RoomWaitingGUI roomGUI = new RoomWaitingGUI(playerName, roomId, player1Name);
                        roomGUI.setVisible(true);
                        setCurrentView(roomGUI);
                    }
                }

                case Protocol.PLAYER_JOINED -> {
                    if (currentView instanceof RoomWaitingGUI && parts.length > 1) {
                        ((RoomWaitingGUI) currentView).onPlayerJoined(parts[1]);
                    }
                }

                case Protocol.GAME_START -> {
                    String opponent = (parts.length > 1) ? parts[1] : "Oponente";

                    // Cerrar vista actual (ShipPlacementGUI)
                    if (currentView != null) {
                        currentView.dispose();
                    }

                    // Abrir GameGUI
                    GameGUI gameGUI = new GameGUI(playerName, opponent);
                    gameGUI.setVisible(true);
                    setCurrentView(gameGUI);
                }

                case "Start_Placing_Ships" -> {
                    // Abrir ShipPlacementGUI independientemente de la vista actual
                    // Esto asegura que ambos jugadores (Player1 y Player2) abran su propia ShipPlacementGUI
                    if (currentView != null) {
                        currentView.dispose();
                    }
                    
                    ShipPlacementGUI shipPlacement = new ShipPlacementGUI(playerName);
                    shipPlacement.setVisible(true);
                    setCurrentView(shipPlacement);
                }

                case "SHIPS_PLACED_OK" -> {
                    if (currentView instanceof ShipPlacementGUI) {
                        ((ShipPlacementGUI) currentView).onShipsPlacedOK();
                    }
                }

                case Protocol.YOUR_TURN -> {
                    if (currentView instanceof GameGUI) {
                        ((GameGUI) currentView).onYourTurn();
                    }
                }

                case Protocol.OPPONENT_TURN -> {
                    if (currentView instanceof GameGUI) {
                        ((GameGUI) currentView).onOpponentTurn();
                    }
                }

                case Protocol.SHOT_RESULT -> {
                    if (currentView instanceof GameGUI && parts.length > 3) {
                        String result = parts[1];
                        int row = Integer.parseInt(parts[2]);
                        int col = Integer.parseInt(parts[3]);
                        ((GameGUI) currentView).onShotResult(result, row, col);
                    }
                }

                case "OPPONENT_SHOT" -> {
                    if (currentView instanceof GameGUI && parts.length > 3) {
                        String result = parts[1];
                        int row = Integer.parseInt(parts[2]);
                        int col = Integer.parseInt(parts[3]);
                        ((GameGUI) currentView).onOpponentShot(result, row, col);
                    }
                }

                case Protocol.SHIP_SUNK -> {
                    if (currentView instanceof GameGUI && parts.length > 4) {
                        int size = Integer.parseInt(parts[1]);
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        String orientation = parts[4];
                        ((GameGUI) currentView).onShipSunk(size, x, y, orientation);
                    }
                }

                case "YOUR_SHIP_SUNK" -> {
                    if (currentView instanceof GameGUI && parts.length > 4) {
                        int size = Integer.parseInt(parts[1]);
                        int x = Integer.parseInt(parts[2]);
                        int y = Integer.parseInt(parts[3]);
                        String orientation = parts[4];
                        ((GameGUI) currentView).onYourShipSunk(size, x, y, orientation);
                    }
                }

                case Protocol.GAME_OVER -> {
                    if (currentView instanceof GameGUI && parts.length > 1) {
                        boolean won = parts[1].equals("WIN");
                        ((GameGUI) currentView).onGameOver(won);
                    }
                }

                default -> System.out.println("Mensaje no manejado: " + command);
            }
        });
    }

    /**
     * Maneja la lógica específica cuando el login es correcto.
     */
    private void handleLoginSuccess(String[] parts) {
        // Formato esperado: LOGIN_OK:Victorias:Derrotas (separados por :)
        // Con split completo: parts[1]=Victorias, parts[2]=Derrotas
        String[] stats = new String[]{"0", "0"};
        if (parts.length > 2) {
            stats[0] = parts[1]; // Victorias
            stats[1] = parts[2]; // Derrotas
        }

        try {
            victorias = Integer.parseInt(stats[0]);
            derrotas = (stats.length > 1) ? Integer.parseInt(stats[1]) : 0;
        } catch (NumberFormatException e) {
            System.err.println("Error parseando estadísticas: " + e.getMessage());
        }

        // Cerrar ventana de Login
        if (currentView != null) {
            currentView.dispose();
        }

        // Abrir ventana del Lobby
        LobbyGUI lobby = new LobbyGUI(playerName, victorias, derrotas);
        lobby.setVisible(true);
        setCurrentView(lobby);

        System.out.println("Login completado. Lobby abierto para " + playerName);
    }

    /**
     * Maneja el registro exitoso
     */
    private void handleRegisterSuccess() {
        if (currentView instanceof RegisterGUI) {
            ((RegisterGUI) currentView).handleRegistrationSuccess();
        }
    }

    /**
     * Maneja el logout exitoso
     */
    private void handleLogoutSuccess() {
        System.out.println("Logout exitoso");

        // Cerrar vista actual
        if (currentView != null) {
            currentView.dispose();
        }

        // Cerrar conexión
        closeConnection();

        // Abrir LoginGUI
        LoginGUI loginGUI = new LoginGUI();
        loginGUI.setVisible(true);
        setCurrentView(loginGUI);
    }

    /**
     * Envía solicitud de logout al servidor
     */
    public void logout() {
        sendMessage(Protocol.LOGOUT);
        // El servidor responderá con LOGOUT_OK que será manejado por handleLogoutSuccess()
    }

    /**
     * Cierra la conexión con el servidor
     */
    public void closeConnection() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
            socket = null;
            out = null;
            in = null;
        } catch (IOException e) {
            System.err.println("Error al cerrar conexión: " + e.getMessage());
        }
    }

    /**
     * Verifica si hay una conexión activa con el servidor
     */
    public boolean isConnected() {
        return socket != null && !socket.isClosed() && socket.isConnected();
    }

    // --- Getters y Setters ---
    public void setCurrentView(JFrame view) {
        this.currentView = view;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
    }

    public String getPlayerName() {
        return playerName;
    }
    
    public void setCurrentRoomId(String roomId) {
        this.currentRoomId = roomId;
    }
    
    public String getCurrentRoomId() {
        return currentRoomId;
    }
    
    public void setVictorias(int victorias) {
        this.victorias = victorias;
    }
    
    public void setDerrotas(int derrotas) {
        this.derrotas = derrotas;
    }
    
    public int getVictorias() {
        return victorias;
    }
    
    public int getDerrotas() {
        return derrotas;
    }
}
