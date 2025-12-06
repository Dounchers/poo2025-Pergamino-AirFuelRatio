package ar.edu.unnoba.poo2025.torneos.exception;

public class UserNotFoundException extends ResourceNotFoundException{
    public UserNotFoundException(String message) {
        super(message);
    }
}
