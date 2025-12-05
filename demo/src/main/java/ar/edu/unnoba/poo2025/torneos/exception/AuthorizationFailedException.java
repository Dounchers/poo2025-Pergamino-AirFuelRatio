package ar.edu.unnoba.poo2025.torneos.exception;

public class AuthorizationFailedException extends RuntimeException {
    public AuthorizationFailedException(String message) {
        super(message);
    }
}

