package ar.edu.unnoba.poo2025.torneos.dto;

import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import ar.edu.unnoba.poo2025.torneos.model.Torneo;
import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InscriptionDetailDTO {
    // Datos de la Inscripción
    private final Long inscriptionId;
    private final BigDecimal price;
    private final LocalDate dateEnrollment;

    // Datos de la Competencia
    private final Long competitionId;
    private final String competitionName;

    // Datos completos del Torneo
    private final Long tournamentId;
    private final String tournamentName;
    private final String tournamentDescription; // Nuevo campo
    private final LocalDate tournamentDateStart; // Nuevo campo
    private final LocalDate tournamentDateEnd;   // Nuevo campo

    public InscriptionDetailDTO(Inscripcion inscripcion) {
        // Mapeo directo
        this.inscriptionId = inscripcion.getId();
        this.price = inscripcion.getPrice();
        this.dateEnrollment = inscripcion.getDateEnrollment();

        // Navegación (por el JOIN FETCH)
        Competencia competencia = inscripcion.getCompetencia();
        Torneo torneo = competencia.getTorneo();

        this.competitionId = competencia.getId();
        this.competitionName = competencia.getName();

        // Mapeo completo del Torneo
        this.tournamentId = torneo.getId();
        this.tournamentName = torneo.getName();
        this.tournamentDescription = torneo.getDescription();
        this.tournamentDateStart = torneo.getDateStart();
        this.tournamentDateEnd = torneo.getDateEnd();
    }

    public BigDecimal getPrice() {
        return price;
    }

    public Long getInscriptionId() {
        return inscriptionId;
    }

    public LocalDate getDateEnrollment() {
        return dateEnrollment;
    }

    public Long getCompetitionId() {
        return competitionId;
    }

    public Long getTournamentId() {
        return tournamentId;
    }

    public String getCompetitionName() {
        return competitionName;
    }

    public String getTournamentName() {
        return tournamentName;
    }

    public String getTournamentDescription() {
        return tournamentDescription;
    }

    public LocalDate getTournamentDateStart() {
        return tournamentDateStart;
    }

    public LocalDate getTournamentDateEnd() {
        return tournamentDateEnd;
    }
}
