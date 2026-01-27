package ar.edu.unnoba.poo2025.torneos.exception;

public class ParticipantNotFoundException extends ResourceNotFoundException {
    public ParticipantNotFoundException(String message) {
        super(message);
    }
}
