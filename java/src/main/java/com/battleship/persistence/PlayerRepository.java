package com.battleship.persistence;

public interface PlayerRepository {
    // Métodos para JPA/Postgres
    boolean authenticate(String user, String pass);
    void updateStats(String user, boolean won);
}