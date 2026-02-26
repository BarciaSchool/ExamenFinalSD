package com.battleship.validation;

import java.util.regex.Pattern;

public class InputValidator {

    private static final Pattern USERNAME_PATTERN = Pattern.compile("^[a-zA-Z0-9_]{3,20}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Za-z])(?=.*\\d).{6,}$");
    private static final int MAX_NAME_LENGTH = 50;

    /**
     * Validate a username
     * @param username The username to validate
     * @return ValidationResult with status and error message if any
     */
    public static ValidationResult validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            return new ValidationResult(false, "El nombre de usuario es requerido");
        }

        if (!USERNAME_PATTERN.matcher(username).matches()) {
            if (username.length() < 3) {
                return new ValidationResult(false, "El usuario debe tener al menos 3 caracteres");
            }
            if (username.length() > 20) {
                return new ValidationResult(false, "El usuario no puede exceder 20 caracteres");
            }
            return new ValidationResult(false, "El usuario solo puede contener letras, números y guiones bajos");
        }

        return new ValidationResult(true, null);
    }

    /**
     * Validate a password
     * @param password The password to validate
     * @return ValidationResult with status and error message if any
     */
    public static ValidationResult validatePassword(String password) {
        if (password == null || password.isEmpty()) {
            return new ValidationResult(false, "La contraseña es requerida");
        }

        if (!PASSWORD_PATTERN.matcher(password).matches()) {
            if (password.length() < 6) {
                return new ValidationResult(false, "La contraseña debe tener al menos 6 caracteres");
            }
            return new ValidationResult(false, "La contraseña debe contener al menos una letra y un número");
        }

        return new ValidationResult(true, null);
    }

    /**
     * Validate a name (nombre or apellido)
     * @param name The name to validate
     * @return ValidationResult with status and error message if any
     */
    public static ValidationResult validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return new ValidationResult(true, null);
        }

        if (name.length() > MAX_NAME_LENGTH) {
            return new ValidationResult(false, "El nombre no puede exceder " + MAX_NAME_LENGTH + " caracteres");
        }

        return new ValidationResult(true, null);
    }

    /**
     * Inner class to represent validation result
     */
    public static class ValidationResult {
        private final boolean valid;
        private final String errorMessage;

        public ValidationResult(boolean valid, String errorMessage) {
            this.valid = valid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return valid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
