package ar.edu.unnoba.poo2025.torneos.exception;

public class DocumentAlreadyRegisteredException extends RuntimeException{
    public DocumentAlreadyRegisteredException(String message) {
        super(message);
    }
}
