package com.battleship.common;

public class Protocol {
    // Comandos Cliente -> Servidor
    public static final String LOGIN = "LOGIN";         // LOGIN:usuario:password
    public static final String REGISTER = "REGISTER";   // REGISTER:usuario:password:nombre:apellido:avatar
    public static final String LOGOUT = "LOGOUT";       // LOGOUT
    public static final String CREATE_ROOM = "CREATE_ROOM";
    public static final String JOIN_ROOM = "JOIN_ROOM"; // JOIN_ROOM:id_sala
    public static final String LIST_ROOMS = "GET_ROOMS";
    public static final String SHOOT = "SHOOT";         // SHOOT:C:5
    public static final String PLACE_SHIPS = "PLACE_SHIPS";

    // Respuestas Servidor -> Cliente
    public static final String LOGIN_OK = "LOGIN_OK";       // LOGIN_OK:victorias:derrotas
    public static final String REGISTER_OK = "REGISTER_OK"; // REGISTER_OK
    public static final String LOGOUT_OK = "LOGOUT_OK";     // LOGOUT_OK
    public static final String ROOM_LIST = "ROOM_LIST";
    public static final String ROOM_CREATED = "ROOM_CREATED"; // ROOM_CREATED:id_sala
    public static final String AUTO_JOINED = "AUTO_JOINED"; // AUTO_JOINED:id_sala (unión automática del creador)
    public static final String JOINED_OK = "JOINED_OK";
    public static final String ROOM_INFO = "ROOM_INFO";     // ROOM_INFO:roomId:player1Name
    public static final String GAME_START = "GAME_START";
    public static final String YOUR_TURN = "YOUR_TURN";
    public static final String OPPONENT_TURN = "OPPONENT_TURN";
    public static final String SHOT_RESULT = "SHOT_RESULT"; // SHOT_RESULT:HIT:C:5
    public static final String SHIP_SUNK = "SHIP_SUNK";     // SHIP_SUNK:tamaño:x,y:orientación
    public static final String GAME_OVER = "GAME_OVER";
    public static final String ERROR = "ERROR";             // ERROR:mensaje

    // Notificaciones entre jugadores
    public static final String PLAYER_JOINED = "PLAYER_JOINED"; // PLAYER_JOINED:nombre_jugador
    public static final String PLAYER_LEFT = "PLAYER_LEFT";   // PLAYER_LEFT:nombre_jugador
}