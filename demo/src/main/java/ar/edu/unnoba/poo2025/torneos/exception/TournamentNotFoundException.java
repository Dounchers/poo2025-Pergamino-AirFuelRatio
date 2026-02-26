package ar.edu.unnoba.poo2025.torneos.exception;

public class TournamentNotFoundException extends ResourceNotFoundException{
    public TournamentNotFoundException(String message) {
        super(message);
    }
}
