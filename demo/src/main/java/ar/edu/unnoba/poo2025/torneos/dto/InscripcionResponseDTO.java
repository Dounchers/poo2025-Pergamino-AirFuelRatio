package ar.edu.unnoba.poo2025.torneos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InscripcionResponseDTO {

    private Long id;
    private Long competenciaId;
    private String competenciaName;
    private BigDecimal price; // El precio final con/sin descuento
    private LocalDate dateEnrollment;

    // Constructor, Getters y Setters...
    public Long getCompetenciaId() {
        return competenciaId;
    }
    public void setCompetenciaId(Long competenciaId) {
        this.competenciaId = competenciaId;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getCompetenciaName() {
        return competenciaName;
    }
    public void setCompetenciaName(String competenciaName) {
        this.competenciaName = competenciaName;
    }

    public BigDecimal getPrice() {
        return price;
    }
    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public LocalDate getDateEnrollment() {
        return dateEnrollment;
    }
    public void setDateEnrollment(LocalDate dateEnrollment) {
        this.dateEnrollment = dateEnrollment;
    }

}