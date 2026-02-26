package com.battleship.model;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "jugadores")
public class Player implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;

    // Campos requeridos por el documento
    private String nombre;
    private String apellido;
    private String avatar; // Puede ser una URL o un identificador de icono (ej. "avatar1")

    private int victorias = 0;
    private int derrotas = 0;

    // Rol: PLAYER o ADMIN
    @Column(nullable = false)
    private String role = "PLAYER";

    // Constructor vacío (Obligatorio JPA)
    public Player() {}

    // Constructor para registro rápido
    public Player(String username, String password) {
        this.username = username;
        this.password = password;
        this.nombre = "SinNombre";
        this.apellido = "SinApellido";
        this.avatar = "default";
    }

    // --- Getters y Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    
    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }
    
    public String getAvatar() { return avatar; }
    public void setAvatar(String avatar) { this.avatar = avatar; }
    
    public int getVictorias() { return victorias; }
    public void setVictorias(int victorias) { this.victorias = victorias; }
    
    public int getDerrotas() { return derrotas; }
    public void setDerrotas(int derrotas) { this.derrotas = derrotas; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public boolean isAdmin() { return "ADMIN".equals(role); }
}