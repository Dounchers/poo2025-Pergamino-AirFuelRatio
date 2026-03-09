package ar.edu.unnoba.poo2025.torneos.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import ar.edu.unnoba.poo2025.torneos.model.Torneo;

@Repository
public interface TournamentRepository extends JpaRepository<Torneo, Long> {
    
    @Query("SELECT t FROM Torneo t WHERE t.publish = true")
    List<Torneo> findPublishedTrue();

    @Override
    @Query("SELECT t FROM Torneo t ORDER BY t.dateStart DESC")
    List<Torneo> findAll();

    @Query("SELECT DISTINCT t FROM Torneo t LEFT JOIN FETCH t.competencias ORDER BY t.dateStart DESC")
    List<Torneo> findAllWithCompetitions();
    
}
