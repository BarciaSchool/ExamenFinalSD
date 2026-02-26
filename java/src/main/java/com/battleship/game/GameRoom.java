package com.battleship.game;

import com.battleship.server.ClientHandler;
import com.battleship.common.Protocol;
import com.battleship.model.Ship;
import java.util.ArrayList;
import java.util.List;

public class GameRoom {
    public enum State { WAITING, PLACING_SHIPS, PLAYING, FINISHED }

    private String roomId;
    private ClientHandler player1;
    private ClientHandler player2;
    private State currentState = State.WAITING;

    // Tableros: 0=Agua, 1=Barco, 2=Tocado, 3=Agua(Fallo)
    private int[][] board1 = new int[8][8];
    private int[][] board2 = new int[8][8];

    // Rastreo de barcos de cada jugador para detección de hundimiento
    private List<Ship> player1Ships = new ArrayList<>();
    private List<Ship> player2Ships = new ArrayList<>();

    // Flags de estado
    private boolean isPlayer1Turn = true;
    private boolean p1ShipsReady = false;
    private boolean p2ShipsReady = false;

    // Estadísticas para el Monitor
    private int p1TotalShots = 0;
    private int p2TotalShots = 0;
    private int p1Hits = 0;
    private int p2Hits = 0;
    private int p1ShipsSunk = 0;
    private int p2ShipsSunk = 0;

    public GameRoom(String roomId, ClientHandler p1) {
        this.roomId = roomId;
        this.player1 = p1;

        // IMPORTANTE: El creador de la sala se une automáticamente a ella
        // Esto permite que: 1) Player2 pueda unirse después
        // 2) El Lobby del creador se cierre o se actualice
        this.currentState = State.WAITING;

        // Notificar al jugador que ha unido a su propia sala
        if (player1 != null) {
            player1.sendMessage("AUTO_JOINED:" + roomId);
            System.out.println("[GameRoom] Creador de sala (" + p1.getPlayerName() + ") unido automáticamente a sala: " + roomId);
        }
    }

    public void addPlayer2(ClientHandler p2) {
        this.player2 = p2;
        this.currentState = State.PLACING_SHIPS;
        
        // Avisar a ambos que coloquen barcos
        player1.sendMessage("Start_Placing_Ships");
        player2.sendMessage("Start_Placing_Ships");
    }

    // --- Lógica de Colocación de Barcos ---

    // Recibe string crudo: "x,y,o;x,y,o;x,y,o;x,y,o;x,y,o"
    // Ejemplo: "0,0,0;2,3,1;5,5,0;8,8,1;12,10,0"
    // Los barcos se envían en orden (0-4) con tamaños: 2, 3, 4, 5, 5
    public synchronized void placeShips(ClientHandler player, String shipData) {
        if (currentState != State.PLACING_SHIPS) return;

        int[][] targetBoard = (player == player1) ? board1 : board2;
        List<Ship> targetShips = (player == player1) ? player1Ships : player2Ships;

        // Parser real de los datos de barcos
        String[] shipStrings = shipData.split(";");

        if (shipStrings.length != 5) {
            player.sendMessage(Protocol.ERROR + ":Debes colocar exactamente 5 barcos");
            return;
        }

        // Limpiar tablero previo y lista de barcos (por si hay datos anteriores)
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                targetBoard[i][j] = 0;
            }
        }
        targetShips.clear();

        // Tamaños de barcos según shipId (0-4) - Ajustados para tablero 8x8
        final int[] SHIP_SIZES = {2, 2, 3, 3, 4};

        // Procesar cada barco
        for (int shipId = 0; shipId < shipStrings.length; shipId++) {
            String shipStr = shipStrings[shipId];
            String[] coords = shipStr.split(",");

            if (coords.length != 3) {
                player.sendMessage(Protocol.ERROR + ":Formato inválido de barcos");
                return;
            }

            try {
                int x = Integer.parseInt(coords[0]);
                int y = Integer.parseInt(coords[1]);
                int orientation = Integer.parseInt(coords[2]);
                int length = SHIP_SIZES[shipId];

                // Verificar límites antes de colocar
                if (x < 0 || x >= 8 || y < 0 || y >= 8) {
                    player.sendMessage(Protocol.ERROR + ":Coordenadas fuera del tablero");
                    return;
                }

                if (orientation == 0 && x + length > 8) {
                    player.sendMessage(Protocol.ERROR + ":Barco sale del tablero por la derecha");
                    return;
                }

                if (orientation == 1 && y + length > 8) {
                    player.sendMessage(Protocol.ERROR + ":Barco sale del tablero por abajo");
                    return;
                }

                // Crear objeto Ship y agregarlo a la lista
                Ship ship = new Ship(shipId, length, x, y, orientation);
                targetShips.add(ship);

                // Colocar barco en el tablero (1 = barco)
                for (int i = 0; i < length; i++) {
                    if (orientation == 0) { // Horizontal
                        targetBoard[y][x + i] = 1;
                    } else { // Vertical
                        targetBoard[y + i][x] = 1;
                    }
                }
            } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                player.sendMessage(Protocol.ERROR + ":Coordenadas inválidas: " + shipStr);
                return;
            }
        }

        if (player == player1) p1ShipsReady = true;
        else p2ShipsReady = true;

        player.sendMessage("SHIPS_PLACED_OK");

        // Si ambos están listos, iniciar combate
        if (p1ShipsReady && p2ShipsReady) {
            startGame();
        }
    }

    private void startGame() {
        currentState = State.PLAYING;
        player1.sendMessage("GAME_START:" + player2.getPlayerName());
        player2.sendMessage("GAME_START:" + player1.getPlayerName());
        notifyTurn();
        RoomManager.getInstance().notifyMonitors(); // Actualizar monitor
    }

    // --- Lógica de Disparo ---

    public synchronized void processShot(ClientHandler shooter, int row, int col) {
        if (currentState != State.PLAYING) return;

        // Validar turno
        if ((shooter == player1 && !isPlayer1Turn) || (shooter == player2 && isPlayer1Turn)) {
            shooter.sendMessage("ERROR:No es tu turno");
            return;
        }

        ClientHandler opponent = (shooter == player1) ? player2 : player1;
        int[][] targetBoard = (shooter == player1) ? board2 : board1;
        List<Ship> targetShips = (shooter == player1) ? player2Ships : player1Ships;
        int[] shooterSunkCount = (shooter == player1) ? new int[]{p1ShipsSunk} : new int[]{p2ShipsSunk};

        // Validar límites y disparo repetido
        if (row < 0 || row > 7 || col < 0 || col > 7 || targetBoard[row][col] >= 2) {
            shooter.sendMessage("ERROR:Coordenada inválida");
            return;
        }

        // Actualizar estadísticas
        if (shooter == player1) {
            p1TotalShots++;
        } else {
            p2TotalShots++;
        }

        // Lógica de impacto
        String result = "MISS";
        if (targetBoard[row][col] == 1) {
            targetBoard[row][col] = 2; // Tocado
            result = "HIT";

            // Actualizar aciertos
            if (shooter == player1) {
                p1Hits++;
            } else {
                p2Hits++;
            }

            // Verificar si se hundió algún barco
            Ship sunkShip = checkShipSunk(targetBoard, targetShips, row, col);
            if (sunkShip != null) {
                // Notificar hundimiento al atacante
                String sunkMsg = String.format("SHIP_SUNK:%d:%d:%d:%s",
                    sunkShip.getLength(),
                    sunkShip.getStartX(),
                    sunkShip.getStartY(),
                    sunkShip.isHorizontal() ? "H" : "V");
                shooter.sendMessage(sunkMsg);

                // Notificar al defensor con un mensaje diferente
                String yourShipSunkMsg = String.format("YOUR_SHIP_SUNK:%d:%d:%d:%s",
                    sunkShip.getLength(),
                    sunkShip.getStartX(),
                    sunkShip.getStartY(),
                    sunkShip.isHorizontal() ? "H" : "V");
                opponent.sendMessage(yourShipSunkMsg);

                // Incrementar contador de barcos hundados
                if (shooter == player1) {
                    p1ShipsSunk++;
                } else {
                    p2ShipsSunk++;
                }
            }

            checkWinCondition(shooter); // Verificar si ganó
        } else {
            targetBoard[row][col] = 3; // Agua (Fallo)
            isPlayer1Turn = !isPlayer1Turn; // Cambio de turno solo si falla
        }

        // Notificar resultados
        shooter.sendMessage("SHOT_RESULT:" + result + ":" + row + ":" + col);
        opponent.sendMessage("OPPONENT_SHOT:" + result + ":" + row + ":" + col);

        if (currentState == State.PLAYING) notifyTurn();
        RoomManager.getInstance().notifyMonitors(); // Actualizar monitor
    }

    private void checkWinCondition(ClientHandler shooter) {
        int[][] targetBoard = (shooter == player1) ? board2 : board1;
        boolean hasShipsLeft = false;

        // Barrido simple: si queda algún '1', sigue el juego
        for (int[] row : targetBoard) {
            for (int cell : row) {
                if (cell == 1) {
                    hasShipsLeft = true;
                    break;
                }
            }
        }

        if (!hasShipsLeft) {
            // Incrementar contador de barcos hundidos del ganador
            if (shooter == player1) {
                p1ShipsSunk = 5; // Todos los barcos hundidos
            } else {
                p2ShipsSunk = 5;
            }
            finishGame(shooter, (shooter == player1 ? player2 : player1), "WIN");
        }
    }

    /**
     * Verifica si algún barco ha sido hundido completamente.
     * Retorna el barco hundido si se hundió uno, null en caso contrario.
     *
     * @param board Tablero del objetivo
     * @param ships Lista de barcos del objetivo
     * @param hitRow Fila del último impacto
     * @param hitCol Columna del último impacto
     * @return El barco hundido, o null si no se hundió ninguno
     */
    private Ship checkShipSunk(int[][] board, List<Ship> ships, int hitRow, int hitCol) {
        // Buscar el barco que fue impactado
        Ship hitShip = null;
        for (Ship ship : ships) {
            int startX = ship.getStartX();
            int startY = ship.getStartY();
            int length = ship.getLength();
            boolean horizontal = ship.isHorizontal();

            // Verificar si el impacto está en este barco
            boolean isHitOnThisShip = false;
            for (int i = 0; i < length; i++) {
                int cellX = horizontal ? startX + i : startX;
                int cellY = horizontal ? startY : startY + i;

                if (cellX == hitCol && cellY == hitRow) {
                    isHitOnThisShip = true;
                    break;
                }
            }

            if (isHitOnThisShip) {
                hitShip = ship;
                break;
            }
        }

        // Si no se encontró el barco impactado, retornar null
        if (hitShip == null) {
            return null;
        }

        // Verificar si todas las celdas del barco están tocadas (valor 2)
        int startX = hitShip.getStartX();
        int startY = hitShip.getStartY();
        int length = hitShip.getLength();
        boolean horizontal = hitShip.isHorizontal();

        boolean allCellsHit = true;
        for (int i = 0; i < length; i++) {
            int cellX = horizontal ? startX + i : startX;
            int cellY = horizontal ? startY : startY + i;

            if (board[cellY][cellX] != 2) {
                allCellsHit = false;
                break;
            }
        }

        // Si todas las celdas están tocadas, el barco se hundió
        if (allCellsHit) {
            // Marcar las celdas como hundidas (valor 4)
            for (int i = 0; i < length; i++) {
                int cellX = horizontal ? startX + i : startX;
                int cellY = horizontal ? startY : startY + i;
                board[cellY][cellX] = 4;
            }
            return hitShip;
        }

        return null;
    }

    private void finishGame(ClientHandler winner, ClientHandler loser, String reason) {
        currentState = State.FINISHED;
        winner.sendMessage("GAME_OVER:WIN");
        if (loser != null) loser.sendMessage("GAME_OVER:LOSE");
        
        // Actualizar Base de Datos
        winner.recordGameResult(true);
        if (loser != null) loser.recordGameResult(false);
        
        RoomManager.getInstance().closeRoom(this);
    }

    // --- Lógica de Desconexión (Robustez) ---

    public synchronized void handlePlayerDisconnect(ClientHandler disconnectedPlayer) {
        if (currentState == State.FINISHED) return; // Ya terminó, no importa

        if (currentState == State.PLAYING || currentState == State.PLACING_SHIPS) {
            ClientHandler winner = (disconnectedPlayer == player1) ? player2 : player1;
            if (winner != null) {
                winner.sendMessage("GAME_OVER:WIN_BY_DISCONNECT");
                winner.recordGameResult(true); // Gana por abandono
                // Opcional: penalizar al que se fue con una derrota
            }
        }
        RoomManager.getInstance().closeRoom(this);
    }

    // --- Getters y Helpers ---
    
    private void notifyTurn() {
        if (currentState != State.PLAYING) return;
        if (isPlayer1Turn) {
            player1.sendMessage("YOUR_TURN");
            player2.sendMessage("OPPONENT_TURN");
        } else {
            player2.sendMessage("YOUR_TURN");
            player1.sendMessage("OPPONENT_TURN");
        }
    }

    public boolean isFull() { return player1 != null && player2 != null; }
    public String getRoomId() { return roomId; }
    public ClientHandler getPlayer1() { return player1; }
    public ClientHandler getPlayer2() { return player2; }
    public State getState() { return currentState; }
    public String getTurnInfo() {
        if (currentState != State.PLAYING) return "-";
        return isPlayer1Turn ? player1.getPlayerName() : player2.getPlayerName();
    }

    // Getters para estadísticas (Monitor)
    public int getP1TotalShots() { return p1TotalShots; }
    public int getP2TotalShots() { return p2TotalShots; }
    public int getP1Hits() { return p1Hits; }
    public int getP2Hits() { return p2Hits; }
    public int getP1ShipsSunk() { return p1ShipsSunk; }
    public int getP2ShipsSunk() { return p2ShipsSunk; }
}