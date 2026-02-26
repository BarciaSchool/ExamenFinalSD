package com.battleship.server;

import com.battleship.common.Protocol;
import com.battleship.game.GameRoom;
import com.battleship.game.RoomManager;
import com.battleship.model.Player;
import com.battleship.persistence.PlayerDAO;
import com.battleship.validation.InputValidator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ClientHandler implements Runnable {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;

    // Identidad y Estado
    private String playerName;
    private boolean isMonitor = false; // Identifica si es el admin 
    private GameRoom currentRoom;

    // Dependencias
    private PlayerDAO playerDAO;

    public ClientHandler(Socket socket) {
        this.socket = socket;
        this.playerDAO = new PlayerDAO(); // Instancia para acceso a BD
    }

    @Override
    public void run() {
        try {
            // Configurar streams de texto
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            String inputLine;
            // Bucle principal de lectura
            while ((inputLine = in.readLine()) != null) {
                processMessage(inputLine);
            }

        } catch (IOException e) {
            // Manejo Crítico: Desconexión abrupta (cerrar ventana, pérdida de red)
            System.err.println("Error de conexión con " + playerName + ": " + e.getMessage());
        } finally {
            handleDisconnect();
        }
    }

    /**
     * Procesa los comandos recibidos según el Protocolo definido.
     */
    private void processMessage(String message) {
        System.out.println("Recibido [" + (playerName != null ? playerName : "Anon") + "]: " + message);

        // Separa comando y argumentos. Ej: "LOGIN:Pepe:123" -> ["LOGIN", "Pepe", "123"]
        String[] parts = message.split(":");
        String command = parts[0];

        switch (command) {
            case Protocol.LOGIN:
                handleLogin(parts);
                break;

            case Protocol.REGISTER:
                handleRegister(parts);
                break;

            case Protocol.LOGOUT:
                handleLogout();
                break;

            case Protocol.CREATE_ROOM:
                if (checkAuth()) {
                    currentRoom = RoomManager.getInstance().createRoom(this);
                    if (currentRoom != null) {
                        sendMessage("ROOM_CREATED:" + currentRoom.getRoomId());

                        // IMPORTANTE: El creador de la sala ya está en la sala desde el constructor
                        // El estado inicial es WAITING y se notifica automáticamente
                        // Esto permite que Player2 pueda unirse después
                    } else {
                        sendMessage(Protocol.ERROR + ":Límite de salas alcanzado");
                    }
                }
                break;

            case Protocol.JOIN_ROOM: // JOIN_ROOM:Sala-1
                if (checkAuth() && parts.length > 1) {
                    boolean joined = RoomManager.getInstance().joinRoom(parts[1], this);
                    if (joined) {
                        sendMessage("JOINED_OK");
                        // currentRoom se asigna dentro del joinRoom o se busca después
                        this.currentRoom = RoomManager.getInstance().getRoomById(parts[1]);
                    } else {
                        sendMessage(Protocol.ERROR + ":Sala llena o no existe");
                    }
                }
                break;

            case Protocol.LIST_ROOMS:
                if (checkAuth()) {
                    String list = RoomManager.getInstance().getRoomListString();
                    sendMessage(Protocol.ROOM_LIST + ":" + list);
                }
                break;

            case Protocol.PLACE_SHIPS: // PLACE_SHIPS:1,1,0;2,2,1... (Coordenadas complejas)
                if (currentRoom != null && parts.length > 1) {
                    // Pasamos la cadena de datos cruda a la sala para que ella la procese
                    currentRoom.placeShips(this, parts[1]);
                }
                break;

            case Protocol.SHOOT: // SHOOT:3:5
                if (currentRoom != null && parts.length > 2) {
                    try {
                        int row = Integer.parseInt(parts[1]);
                        int col = Integer.parseInt(parts[2]);
                        currentRoom.processShot(this, row, col);
                    } catch (NumberFormatException e) {
                        sendMessage(Protocol.ERROR + ":Coordenadas inválidas");
                    }
                }
                break;

            default:
                sendMessage(Protocol.ERROR + ":Comando desconocido");
                break;
        }
    }

    /**
     * Maneja la autenticación contra PostgreSQL usando el DAO.
     */
    private void handleLogin(String[] parts) {
        // Protocolo esperado: LOGIN:usuario:password
        if (parts.length < 3) {
            sendMessage(Protocol.ERROR + ":Faltan datos de login");
            return;
        }

        String user = parts[1];
        String pass = parts[2];

        // Caso Admin (Monitor) - Verificar en BD si tiene rol ADMIN
        if (user.equalsIgnoreCase("ADMIN") && pass.equals("admin123")) {
            // Admin fallback por compatibilidad temporal
            this.playerName = "ADMIN";
            this.isMonitor = true;
            sendMessage(Protocol.LOGIN_OK + ":0:0:ADMIN");
            RoomManager.getInstance().addMonitor(this);
            return;
        }

        // Caso Jugador Normal o Admin con BD
        Player player = playerDAO.login(user, pass);

        if (player != null) {
            this.playerName = player.getUsername();

            // Verificar si es ADMIN
            if (player.isAdmin()) {
                this.isMonitor = true;
                sendMessage(Protocol.LOGIN_OK + ":0:0:ADMIN");
                RoomManager.getInstance().addMonitor(this);
            } else {
                // Jugador normal
                sendMessage(Protocol.LOGIN_OK + ":" + player.getVictorias() + ":" + player.getDerrotas());
            }
        } else {
            // Verificar si el usuario existe para dar mensaje apropiado
            if (playerDAO.usernameExists(user)) {
                sendMessage(Protocol.ERROR + ":Contraseña incorrecta");
            } else {
                sendMessage(Protocol.ERROR + ":Usuario no existe");
            }
        }
    }

    /**
     * Maneja el registro de nuevos usuarios
     * Protocolo: REGISTER:username:password:nombre:apellido:avatar
     */
    private void handleRegister(String[] parts) {
        if (parts.length < 6) {
            sendMessage(Protocol.ERROR + ":Faltan datos de registro");
            return;
        }

        String username = parts[1];
        String password = parts[2];
        String nombre = parts[3];
        String apellido = parts[4];
        String avatar = parts[5];

        // Validación del lado del servidor
        InputValidator.ValidationResult usernameValidation = InputValidator.validateUsername(username);
        if (!usernameValidation.isValid()) {
            sendMessage(Protocol.ERROR + ":" + usernameValidation.getErrorMessage());
            return;
        }

        InputValidator.ValidationResult passwordValidation = InputValidator.validatePassword(password);
        if (!passwordValidation.isValid()) {
            sendMessage(Protocol.ERROR + ":" + passwordValidation.getErrorMessage());
            return;
        }

        InputValidator.ValidationResult nombreValidation = InputValidator.validateName(nombre);
        if (!nombreValidation.isValid()) {
            sendMessage(Protocol.ERROR + ":" + nombreValidation.getErrorMessage());
            return;
        }

        InputValidator.ValidationResult apellidoValidation = InputValidator.validateName(apellido);
        if (!apellidoValidation.isValid()) {
            sendMessage(Protocol.ERROR + ":" + apellidoValidation.getErrorMessage());
            return;
        }

        // Intentar registrar
        Player newPlayer = playerDAO.register(username, password, nombre, apellido, avatar);

        if (newPlayer != null) {
            sendMessage(Protocol.REGISTER_OK);
        } else {
            sendMessage(Protocol.ERROR + ":Usuario ya existe");
        }
    }

    /**
     * Maneja el logout del usuario
     */
    private void handleLogout() {
        if (playerName != null) {
            System.out.println("Logout de usuario: " + playerName);
        }

        // Limpiar sesión
        playerName = null;
        isMonitor = false;

        // Enviar confirmación
        sendMessage(Protocol.LOGOUT_OK);

        // No cerrar socket aquí - dejar que el cliente lo cierre
    }

    /**
     * Limpieza robusta cuando el cliente se desconecta. Evita que las salas
     * queden "zombies".
     */
    private void handleDisconnect() {
        System.out.println("Cerrando sesión de: " + playerName);

        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (isMonitor) {
            RoomManager.getInstance().removeMonitor(this);
        } else if (currentRoom != null) {
            // Notificar a la sala que el jugador se fue
            currentRoom.handlePlayerDisconnect(this);
        }
    }

    // --- Métodos Auxiliares ---
    public void sendMessage(String msg) {
        if (out != null) {
            out.println(msg);
        }
    }

    private boolean checkAuth() {
        if (playerName == null) {
            sendMessage(Protocol.ERROR + ":Debes hacer LOGIN primero");
            return false;
        }
        return true;
    }

    public String getPlayerName() {
        return playerName;
    }

    // Método para actualizar BD al terminar partida (llamado desde GameRoom)
    public void recordGameResult(boolean won) {
        if (won) {
            playerDAO.registrarVictoria(playerName);
        } else {
            playerDAO.registrarDerrota(playerName);
        }
    }

}
