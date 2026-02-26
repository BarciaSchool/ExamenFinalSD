package com.battleship.persistence;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;

/**
 * Maneja la creación de EntityManagerFactory con variables de entorno
 * Soporta tanto Docker (variables de entorno) como desarrollo local (valores por defecto)
 */
public class PersistenceManager {

    private static EntityManagerFactory emf;

    public static EntityManagerFactory getEntityManagerFactory() {
        if (emf == null) {
            synchronized (PersistenceManager.class) {
                if (emf == null) {
                    emf = createEntityManagerFactory();
                }
            }
        }
        return emf;
    }

    private static EntityManagerFactory createEntityManagerFactory() {
        Map<String, Object> properties = new HashMap<>();

        // Leer variables de entorno o usar valores por defecto
        String dbUrl = getEnv("DB_URL", "jdbc:postgresql://localhost:54322/battleship_db");
        String dbUser = getEnv("DB_USER", "postgres");
        String dbPassword = getEnv("DB_PASSWORD", "admin");

        System.out.println("[PersistenceManager] Conectando a BD: " + dbUrl);

        properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("javax.persistence.jdbc.url", dbUrl);
        properties.put("javax.persistence.jdbc.user", dbUser);
        properties.put("javax.persistence.jdbc.password", dbPassword);
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect");
        properties.put("hibernate.hbm2ddl.auto", "update");
        properties.put("hibernate.show_sql", "true");

        return Persistence.createEntityManagerFactory("battleshipPU", properties);
    }

    private static String getEnv(String key, String defaultValue) {
        String value = System.getenv(key);
        if (value == null || value.trim().isEmpty()) {
            System.out.println("[PersistenceManager] " + key + " no definida, usando default: " + defaultValue);
            return defaultValue;
        }
        return value;
    }

    public static void close() {
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
}
