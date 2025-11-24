package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;  
import ar.edu.unnoba.poo2025.torneos.model.Torneo;

public interface TournamentService {
    public List<Torneo> getPublishedTournaments();
}
