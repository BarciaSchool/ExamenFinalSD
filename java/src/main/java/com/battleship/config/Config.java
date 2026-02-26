package com.battleship.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Clase para leer la configuración de red desde config.properties
 */
public class Config {

    private static Properties properties = new Properties();
    private static boolean loaded = false;

    // Valores por defecto
    private static final String DEFAULT_HOST = "localhost";
    private static final int DEFAULT_PORT = 9090;

    /**
     * Carga el archivo config.properties si no ha sido cargado aún
     */
    private static void load() {
        if (loaded) return;

        try (InputStream input = new FileInputStream("config.properties")) {
            properties.load(input);
            loaded = true;
            System.out.println("[Config] Configuración cargada desde config.properties");
        } catch (IOException e) {
            System.out.println("[Config] No se encontró config.properties, usando defaults");
            System.out.println("[Config] host=" + DEFAULT_HOST + ", port=" + DEFAULT_PORT);
            loaded = true;
        }
    }

    /**
     * Obtiene el host del servidor
     */
    public static String getServerHost() {
        load();
        return properties.getProperty("server.host", DEFAULT_HOST);
    }

    /**
     * Obtiene el puerto del servidor
     */
    public static int getServerPort() {
        load();
        String portStr = properties.getProperty("server.port", String.valueOf(DEFAULT_PORT));
        try {
            return Integer.parseInt(portStr);
        } catch (NumberFormatException e) {
            return DEFAULT_PORT;
        }
    }

    /**
     * Obtiene la URL de conexión (host:port)
     */
    public static String getServerAddress() {
        return getServerHost() + ":" + getServerPort();
    }
}
