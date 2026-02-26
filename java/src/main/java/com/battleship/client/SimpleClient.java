package com.battleship.client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class SimpleClient {
    public static void main(String[] args) {
        System.out.println("--- Conectando al Servidor ---");
        
        // 1. Conectarse al localhost puerto 9090
        try (Socket socket = new Socket("localhost", 9090)) {
            
            // 2. Preparar canales de lectura/escritura
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            // 3. ENVIAR comando de Login (Simulamos lo que harías por teclado)
            String mensaje = "LOGIN:UsuarioWindows:1234";
            System.out.println("Enviando: " + mensaje);
            out.println(mensaje);

            // 4. LEER respuesta del servidor
            String respuesta = in.readLine();
            System.out.println("Respuesta del Servidor: " + respuesta);
            
            // Si funciona, deberías ver: "LOGIN_OK:0:0"
            
        } catch (Exception e) {
            System.err.println("Error: No se pudo conectar.");
            System.err.println("Asegúrate de que ServerMain.java esté corriendo.");
        }
    }
}