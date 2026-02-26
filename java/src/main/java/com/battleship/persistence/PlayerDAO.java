package com.battleship.persistence;

import com.battleship.model.Player;
import com.battleship.security.PasswordUtil;
import javax.persistence.*;

public class PlayerDAO {
    // Usa PersistenceManager para leer variables de entorno
    private static EntityManagerFactory emf = PersistenceManager.getEntityManagerFactory();

    /**
     * Login method - authenticates user without auto-registration
     * Supports automatic migration from plain-text to bcrypt passwords
     * @param username The username
     * @param password The plain-text password
     * @return Player object if authentication succeeds, null otherwise
     */
    public Player login(String username, String password) {
        EntityManager em = emf.createEntityManager();
        try {
            // 1. Buscar si el usuario existe
            try {
                Player p = em.createQuery("SELECT p FROM Player p WHERE p.username = :user", Player.class)
                             .setParameter("user", username)
                             .getSingleResult();

                // 2. Verificar contraseña (con soporte de migración)
                String storedPassword = p.getPassword();

                if (PasswordUtil.isBcryptHash(storedPassword)) {
                    // Password ya está hasheado - verificar con bcrypt
                    if (PasswordUtil.verifyPassword(password, storedPassword)) {
                        return p;
                    } else {
                        return null; // Contraseña incorrecta
                    }
                } else {
                    // Password está en texto plano - migrar automáticamente
                    if (storedPassword.equals(password)) {
                        // Password correcta - hashear y actualizar
                        migratePasswordToHash(p.getId(), password);
                        return p;
                    } else {
                        return null; // Contraseña incorrecta
                    }
                }
            } catch (NoResultException e) {
                // Usuario no existe
                return null;
            }
        } finally {
            em.close();
        }
    }

    /**
     * Register a new user with all profile information
     * @param username The username (must be unique)
     * @param password The plain-text password (will be hashed)
     * @param nombre First name (optional)
     * @param apellido Last name (optional)
     * @param avatar Avatar identifier
     * @return The newly created Player object, or null if username already exists
     */
    public Player register(String username, String password, String nombre, String apellido, String avatar) {
        if (usernameExists(username)) {
            return null; // Username already taken
        }

        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();

            // Hash password before storing
            String hashedPassword = PasswordUtil.hashPassword(password);

            // Create player with all fields
            Player nuevo = new Player(username, hashedPassword);
            nuevo.setNombre(nombre != null && !nombre.trim().isEmpty() ? nombre : "Jugador");
            nuevo.setApellido(apellido != null && !apellido.trim().isEmpty() ? apellido : "");
            nuevo.setAvatar(avatar != null && !avatar.trim().isEmpty() ? avatar : "default");

            em.persist(nuevo);
            em.getTransaction().commit();
            return nuevo;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
            return null;
        } finally {
            em.close();
        }
    }

    /**
     * Check if a username already exists
     * @param username The username to check
     * @return true if exists, false otherwise
     */
    public boolean usernameExists(String username) {
        EntityManager em = emf.createEntityManager();
        try {
            Long count = em.createQuery("SELECT COUNT(p) FROM Player p WHERE p.username = :user", Long.class)
                           .setParameter("user", username)
                           .getSingleResult();
            return count > 0;
        } finally {
            em.close();
        }
    }

    /**
     * Migrate a plain-text password to bcrypt hash
     * Used for automatic migration during login
     * @param playerId The player's ID
     * @param plainPassword The plain-text password to hash
     */
    private void migratePasswordToHash(Long playerId, String plainPassword) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Player p = em.find(Player.class, playerId);
            if (p != null) {
                String hashedPassword = PasswordUtil.hashPassword(plainPassword);
                p.setPassword(hashedPassword);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
    
    public void registrarVictoria(String username) {
        actualizarStat(username, true);
    }
    
    public void registrarDerrota(String username) {
        actualizarStat(username, false);
    }

    private void actualizarStat(String username, boolean esVictoria) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Player p = em.createQuery("SELECT p FROM Player p WHERE p.username = :user", Player.class)
                         .setParameter("user", username)
                         .getSingleResult();
            
            if (esVictoria) p.setVictorias(p.getVictorias() + 1);
            else p.setDerrotas(p.getDerrotas() + 1);
            
            em.getTransaction().commit();
        } catch (Exception e) {
            if(em.getTransaction().isActive()) em.getTransaction().rollback();
            e.printStackTrace();
        } finally {
            em.close();
        }
    }
}