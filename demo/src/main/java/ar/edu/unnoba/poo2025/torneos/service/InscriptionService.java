package ar.edu.unnoba.poo2025.torneos.service;

import java.util.List;

import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;

public interface InscriptionService {
    /**
     * Inscribe a un participante en una competencia, aplicando todas las reglas de negocio
     * (publicado, cupo, fecha, unicidad y descuento).
     * @param tournamentId ID del torneo.
     * @param competitionId ID de la competencia.
     * @param participantId ID del participante.
     * @return El objeto Inscripcion recién creado y persistido.
     */

    public Inscripcion inscribeParticipant(Long tournamentId, Long competitionId, Long participantId);

    public List<Inscripcion> findInscriptionsByParticipant(Long participantId);

    public Inscripcion findInscriptionDetailsByIdAndParticipantId(Long inscriptionId, Long participantId);
}
