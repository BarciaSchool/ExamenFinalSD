package com.battleship.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    private static final int PORT = 9090;

    public static void main(String[] args) {
        System.out.println("Iniciando Servidor Batalla Naval en puerto " + PORT);
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            
            while (true) {
                Socket clientSocket = serverSocket.accept(); // Espera conexión
                System.out.println("Nuevo cliente conectado: " + clientSocket.getInetAddress());
                
                // Crea un hilo por cada cliente [cite: 21]
                ClientHandler handler = new ClientHandler(clientSocket);
                new Thread(handler).start();
            }
            
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}