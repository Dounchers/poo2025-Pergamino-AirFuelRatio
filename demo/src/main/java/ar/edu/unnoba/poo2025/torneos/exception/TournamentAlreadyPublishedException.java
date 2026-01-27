package ar.edu.unnoba.poo2025.torneos.exception;

public class TournamentAlreadyPublishedException extends ResourceNotFoundException {
    public TournamentAlreadyPublishedException(String message) {
        super(message);
    }
}
