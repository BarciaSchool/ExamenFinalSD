package com.battleship.monitor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Cliente que se conecta al servidor como Monitor (ADMIN).
 * Recibe actualizaciones en tiempo real del estado de las salas.
 */
public class MonitorClient {

    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private MonitorDataListener listener;
    private boolean running = false;

    /**
     * Interface para notificar a la GUI cuando llegan datos nuevos
     */
    public interface MonitorDataListener {
        void onDataReceived(String data);
        void onConnectionError(String error);
    }

    public MonitorClient(MonitorDataListener listener) {
        this.listener = listener;
    }

    /**
     * Conecta al servidor y se autentica como ADMIN
     */
    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            out = new PrintWriter(socket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // Autenticarse como ADMIN
            out.println("LOGIN:ADMIN:admin123");

            // Leer respuesta de login
            String response = in.readLine();
            if (response != null && response.startsWith("LOGIN_OK")) {
                // Login exitoso, iniciar hilo de escucha
                running = true;
                new Thread(this::listenForUpdates).start();
                return true;
            } else {
                listener.onConnectionError("Error de autenticación: " + response);
                return false;
            }

        } catch (IOException e) {
            listener.onConnectionError("No se pudo conectar al servidor: " + e.getMessage());
            return false;
        }
    }

    /**
     * Hilo que escucha constantemente los mensajes del servidor
     */
    private void listenForUpdates() {
        try {
            String line;
            while (running && (line = in.readLine()) != null) {
                if (line.startsWith("MONITOR_DATA:")) {
                    // Extraer los datos después de "MONITOR_DATA:"
                    String data = line.substring(13);
                    listener.onDataReceived(data);
                }
            }
        } catch (IOException e) {
            if (running) {
                listener.onConnectionError("Conexión perdida: " + e.getMessage());
            }
        }
    }

    /**
     * Cierra la conexión con el servidor
     */
    public void disconnect() {
        running = false;
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean isConnected() {
        return socket != null && socket.isConnected() && !socket.isClosed();
    }
}
