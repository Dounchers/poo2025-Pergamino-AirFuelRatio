package ar.edu.unnoba.poo2025.torneos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InscripcionDTO {
    private Long id;
    private BigDecimal price;
    private LocalDate dateEnrollment;
    private String participantEmail;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getDateEnrollment() { return dateEnrollment; }
    public void setDateEnrollment(LocalDate dateEnrollment) { this.dateEnrollment = dateEnrollment; }
    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }
}
