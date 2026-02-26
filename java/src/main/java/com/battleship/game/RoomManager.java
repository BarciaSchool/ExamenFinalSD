package com.battleship.game;

import com.battleship.server.ClientHandler;
import com.battleship.common.Protocol;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class RoomManager {
    private static RoomManager instance;
    private List<GameRoom> rooms;
    // Lista segura para hilos para los monitores (Admins)
    private List<ClientHandler> monitors; 

    private RoomManager() {
        rooms = new CopyOnWriteArrayList<>(); // Lista thread-safe
        monitors = new CopyOnWriteArrayList<>();
    }

    public static synchronized RoomManager getInstance() {
        if (instance == null) instance = new RoomManager();
        return instance;
    }

    // --- Gestión de Salas ---

    public synchronized GameRoom createRoom(ClientHandler player) {
        if (rooms.size() >= 4) return null;
        
        String id = "Sala-" + (rooms.size() + 1);
        GameRoom room = new GameRoom(id, player);
        rooms.add(room);
        notifyMonitors(); // Avisar al admin que hay nueva sala
        return room;
    }

    public synchronized boolean joinRoom(String roomId, ClientHandler player) {
        GameRoom room = getRoomById(roomId);
        if (room != null && !room.isFull()) {
            // Notificar al jugador existente (Player1) que alguien se va a unir
            ClientHandler player1 = room.getPlayer1();
            if (player1 != null) {
                player1.sendMessage(Protocol.PLAYER_JOINED + ":" + player.getPlayerName());
                
                // Enviar información de la sala al nuevo jugador (Player2)
                // Formato: ROOM_INFO:roomId:player1Name
                player.sendMessage("ROOM_INFO:" + roomId + ":" + player1.getPlayerName());
            }

            // Agregar el nuevo jugador (Player2)
            room.addPlayer2(player);
            notifyMonitors(); // Avisar cambio de estado
            return true;
        }
        return false;
    }

    public GameRoom getRoomById(String id) {
        return rooms.stream()
                .filter(r -> r.getRoomId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Formato simplificado para enviar al cliente: "ID,J1,J2,Estado,Jugadores|ID,J1,J2,Estado,Jugadores|..."
    // Ejemplo: "Sala-1,player1,---,WAITING,1/2|Sala-1,player1,jugador2,PLACING_SHIPS,2/2|"
    public String getRoomListString() {
        StringBuilder sb = new StringBuilder();
        for (GameRoom room : rooms) {
            sb.append(room.getRoomId()).append(",");
            sb.append(room.getPlayer1() != null ? room.getPlayer1().getPlayerName() : "---").append(",");
            sb.append(room.getPlayer2() != null ? room.getPlayer2().getPlayerName() : "---").append(",");
            sb.append(room.getState().name()).append(",");

            // Calcular cantidad de jugadores
            int playerCount = 0;
            if (room.getPlayer1() != null) playerCount++;
            if (room.getPlayer2() != null) playerCount++;
            sb.append(playerCount).append("/2");

            sb.append("|"); // Separador de salas
        }

        // Eliminar el último separador si existe
        String result = sb.toString();
        if (result.endsWith("|")) {
            result = result.substring(0, result.length() - 1);
        }

        // Debug
        System.out.println("[RoomManager] Enviando ROOM_LIST: " + result);

        return result;
    }

    public synchronized void closeRoom(GameRoom room) {
        rooms.remove(room);
        notifyMonitors();
    }

    // --- Gestión de Monitores (Admin) ---

    public void addMonitor(ClientHandler monitor) {
        monitors.add(monitor);
        // Enviar estado actual inmediatamente
        monitor.sendMessage("MONITOR_DATA:" + getMonitorData());
    }

    public void removeMonitor(ClientHandler monitor) {
        monitors.remove(monitor);
    }

    // Genera string con estado de todas las salas para el Admin
    // Formato: ID|Jugador1|Jugador2|Estado|Turno|P1Shots|P1Hits|P1Sunk|P2Shots|P2Hits|P2Sunk;...
    private String getMonitorData() {
        StringBuilder sb = new StringBuilder();
        for (GameRoom r : rooms) {
            String p1Name = (r.getPlayer1() != null) ? r.getPlayer1().getPlayerName() : "---";
            String p2Name = (r.getPlayer2() != null) ? r.getPlayer2().getPlayerName() : "---";
            String turnInfo = (r.getPlayer1() != null) ? r.getTurnInfo() : "---";

            sb.append(r.getRoomId()).append("|")
              .append(p1Name).append("|")
              .append(p2Name).append("|")
              .append(r.getState()).append("|")
              .append(turnInfo).append("|")
              .append(r.getP1TotalShots()).append("|")
              .append(r.getP1Hits()).append("|")
              .append(r.getP1ShipsSunk()).append("|")
              .append(r.getP2TotalShots()).append("|")
              .append(r.getP2Hits()).append("|")
              .append(r.getP2ShipsSunk())
              .append(";");
        }
        return sb.toString();
    }

    // Notifica a todos los admins conectados cuando algo cambia
    public void notifyMonitors() {
        String data = getMonitorData();
        for (ClientHandler m : monitors) {
            m.sendMessage("MONITOR_DATA:" + data);
        }
    }
}