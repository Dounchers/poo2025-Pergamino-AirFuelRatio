package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.repository.TournamentRepository;

@Service
public class TournamentServiceImp implements TournamentService {
    
    @Autowired
    private TournamentRepository tournamentRepository;

    @Override
    public List<Torneo> getPublishedTournaments() {
        return tournamentRepository.findPublishedTrue();
    }

    @Override
    public Torneo findById(Long id){
        return tournamentRepository.findById(id).orElse(null);
    }
}