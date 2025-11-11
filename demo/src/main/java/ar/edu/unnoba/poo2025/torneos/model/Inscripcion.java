package ar.edu.unnoba.poo2025.torneos.model;
import java.math.BigDecimal;
import java.time.LocalDate;

import jakarta.persistence.*;

@Entity
@Table(name = "inscripcion", uniqueConstraints = {
    //Un participante, una única vez por competencia
   @UniqueConstraint(columnNames = {"participante_id", "competencia_id"})
})
public class Inscripcion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "participante_id", nullable = false)
    private Participante participante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "competencia_id", nullable = false)
    private Competencia competencia;

    @Column(name = "price", precision = 10, scale = 2, nullable = false)
    private BigDecimal price;

    @Column(name = "date_enrollment", nullable = false)
    private LocalDate dateEnrollment;

    public Inscripcion() {}

    // --- MÉTODOS DE NEGOCIO ---

    /**
     * Calcula el precio final de la inscripción aplicando el descuento.
     * Esta lógica debe ser llamada al crear la inscripción.
     * @param isFirstEnrollment Indica si esta es la primera competencia del participante en el TORNEO.
     */
    public void calculatePrice(boolean isFirstEnrollment) {
        if (this.competencia == null) {
            throw new IllegalStateException("La competencia debe estar asignada para calcular el precio.");
        }
        // Delega la lógica del descuento a la Competencia
        this.price = this.competencia.applyDiscount(isFirstEnrollment);
    }
    
    // --- Getters y Setters...
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Participante getParticipante() { return participante; }
    public void setParticipante(Participante participante) { this.participante = participante; }
    public Competencia getCompetencia() { return competencia; }
    public void setCompetencia(Competencia competencia) { this.competencia = competencia; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public LocalDate getDateEnrollment() { return dateEnrollment; }
    public void setDateEnrollment(LocalDate dateEnrollment) { this.dateEnrollment = dateEnrollment; }
}