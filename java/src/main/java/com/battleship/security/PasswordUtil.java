package com.battleship.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordUtil {

    private static final int BCRYPT_COST_FACTOR = 12;

    /**
     * Hash a plain text password using BCrypt
     * @param plainText The password to hash
     * @return The BCrypt hashed password
     */
    public static String hashPassword(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null or empty");
        }
        return BCrypt.hashpw(plainText, BCrypt.gensalt(BCRYPT_COST_FACTOR));
    }

    /**
     * Verify a plain text password against a BCrypt hash
     * @param plainText The plain text password to verify
     * @param hashed The BCrypt hash to verify against
     * @return true if the password matches, false otherwise
     */
    public static boolean verifyPassword(String plainText, String hashed) {
        if (plainText == null || hashed == null) {
            return false;
        }
        try {
            return BCrypt.checkpw(plainText, hashed);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Check if a password string is already a BCrypt hash
     * @param password The password string to check
     * @return true if it's a BCrypt hash, false otherwise
     */
    public static boolean isBcryptHash(String password) {
        if (password == null) {
            return false;
        }
        return password.startsWith("$2a$") && password.length() == 60;
    }
}
