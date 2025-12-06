package ar.edu.unnoba.poo2025.torneos.exception;

public class TournamentNotFoundException extends RuntimeException{
    public TournamentNotFoundException(String message) {
        super(message);
    }
}
