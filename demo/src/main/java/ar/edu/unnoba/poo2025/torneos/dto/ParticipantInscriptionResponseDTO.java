package ar.edu.unnoba.poo2025.torneos.dto;

import ar.edu.unnoba.poo2025.torneos.model.Inscripcion;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;


public class ParticipantInscriptionResponseDTO {
    // De la inscripción
    private final Long id;
    private final LocalDate dateEnrollment;
    private final BigDecimal price;

    // Del torneo
    private final Long torneoId;
    private final String torneoName;

    // De la competencia
    private final Long competenciaId;
    private final String competenciaName;

    public ParticipantInscriptionResponseDTO(Inscripcion inscripcion) {
        this.id = inscripcion.getId();
        this.dateEnrollment = inscripcion.getDateEnrollment();
        this.price = inscripcion.getPrice();

        // Las relaciones ya están cargadas gracias al query optimizado en el repositorio.
        this.torneoId = inscripcion.getCompetencia().getTorneo().getId();
        this.torneoName = inscripcion.getCompetencia().getTorneo().getName();
        this.competenciaId = inscripcion.getCompetencia().getId();
        this.competenciaName = inscripcion.getCompetencia().getName();
    }

    // En un DTO inmutable (usando final), solo getters
    public Long getId() { return id; }
    public LocalDate getDateEnrollment() { return dateEnrollment; }
    public BigDecimal getPrice() { return price; }
    public Long getTorneoId() { return torneoId; }
    public String getTorneoName() { return torneoName; }
    public Long getCompetenciaId() { return competenciaId; }
    public String getCompetenciaName() { return competenciaName; }

}
