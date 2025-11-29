package ar.edu.unnoba.poo2025.torneos.service;

import java.math.BigDecimal;
import java.util.List;

import ar.edu.unnoba.poo2025.torneos.model.Torneo;

public interface TournamentService {
    public List<Torneo> getPublishedTournaments();
    
    public Torneo findById(Long id);
    public void delete(Long id) throws Exception;
    
    public void publish(Long id) throws Exception;

    public List<Torneo> findAll();

    public Torneo create(Torneo torneo) throws Exception;

    public Torneo update(Long id, Torneo torneo) throws Exception;

    public Long getTotalEnrollments(Long tournamentId) throws Exception;

    public BigDecimal getTotalRevenue(Long tournamentId) throws Exception;

}