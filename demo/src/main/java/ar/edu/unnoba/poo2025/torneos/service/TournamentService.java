package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;  
import ar.edu.unnoba.poo2025.torneos.model.Torneo;

public interface TournamentService {
    public List<Torneo> getPublishedTournaments();
    public Torneo findById(Long id);
    public void delete(Long id) throws Exception;
    public void publish(Long id) throws Exception;
}
