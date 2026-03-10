package ar.edu.unnoba.poo2025.torneos.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public class InscripcionDTO {
    private Long id;
    private BigDecimal price;
    private LocalDate dateEnrollment;
    private String participantEmail;
    private String participantFirstName;
    private String participantLastName;
    private String participantDocumentType;
    private String participantDocumentNumber;

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getDateEnrollment() { return dateEnrollment; }
    public void setDateEnrollment(LocalDate dateEnrollment) { this.dateEnrollment = dateEnrollment; }
    public String getParticipantEmail() { return participantEmail; }
    public void setParticipantEmail(String participantEmail) { this.participantEmail = participantEmail; }
    public String getParticipantFirstName() { return participantFirstName; }
    public void setParticipantFirstName(String participantFirstName) { this.participantFirstName = participantFirstName; }
    public String getParticipantLastName() { return participantLastName; }
    public void setParticipantLastName(String participantLastName) { this.participantLastName = participantLastName; }
    public String getParticipantDocumentType() { return participantDocumentType; }
    public void setParticipantDocumentType(String participantDocumentType) { this.participantDocumentType = participantDocumentType; }
    public String getParticipantDocumentNumber() { return participantDocumentNumber; }
    public void setParticipantDocumentNumber(String participantDocumentNumber) { this.participantDocumentNumber = participantDocumentNumber; }
}
