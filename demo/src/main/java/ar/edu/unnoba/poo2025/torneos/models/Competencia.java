package ar.edu.unnoba.poo2025.torneos.models;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Entity
@Table(name = "competencia", uniqueConstraints = {
    // El DER no especifica clave única, pero a menudo tiene sentido que el nombre
    // sea único por torneo. Por simplicidad, nos apegamos al DER que no lo tiene.
})
public class Competencia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponde a id: long del UML y INTEGER [PK] del DER

    @Column(name = "name", length = 255, nullable = false)
    private String name; // Corresponde a name: string del UML y VARCHAR(255) NOT NULL del DER

    // basePrice: BigDecimal para manejo preciso de moneda.
    @Column(name = "base_price", precision = 10, scale = 2, nullable = false)
    private BigDecimal basePrice; // Corresponde a basePrice: Bigdecimal del UML y DECIMAL(10,2) NOT NULL del DER

    @Column(name = "capacity", nullable = false)
    private Integer capacity; // Corresponde a capacity: integer del UML y INTEGER NOT NULL del DER

    // Relación con Torneo: Competencia (1,n) pertenece a Torneo (1,1)
    // El DER indica torneo_id INTEGER [FK] NOT NULL.
    @ManyToOne(fetch = FetchType.LAZY) // Muchas Competencias pertenecen a un Torneo
    @JoinColumn(name = "torneo_id", nullable = false)
    private Torneo torneo;

    // Relación con Inscripción: Competencia (1,1) asigna Inscripción (0,n)
    // Se mapea con el atributo 'competencia' en la clase Inscripcion.
    @OneToMany(mappedBy = "competencia", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Inscripcion> inscripciones = new ArrayList<>(); // Inicializar para evitar NullPointerException

    // Constructor vacío (obligatorio para JPA)
    public Competencia() {
    }

    // --- Métodos de Negocio (Operaciones del UML) ---

    // hasCapacityAvailable(): boolean - Del UML: Verifica si el cupo no ha sido alcanzado.
    public boolean hasCapacityAvailable() {
        // La lista de inscripciones es una colección persistente.
        // Se asume que getInscripciones() devuelve la lista actual de inscripciones persistidas.
        return this.inscripciones.size() < this.capacity;
    }

    // getEnrolledParticipants(): List<Participante> - Del UML: Devuelve la lista de participantes inscriptos.
    public List<Participante> getEnrolledParticipants() {
        // Mapea la lista de Inscripcion a una lista de Participante.
        return this.inscripciones.stream()
                .map(Inscripcion::getParticipante)
                .collect(Collectors.toList());
    }

    // applyDiscount(isFirstEnrollment: boolean): BigDecimal - Del UML: Calcula el precio con descuento.
    // Lógica de negocio: "si un participante se inscribe en más de una competencia dentro del mismo torneo,
    // la primera se paga completa y las siguientes al 50%."
    public BigDecimal applyDiscount(boolean isFirstEnrollment) {
        if (isFirstEnrollment) {
            return this.basePrice; // Primera inscripción: 100%
        } else {
            // Aplicar 50% de descuento. Se usa BigDecimal para operaciones precisas.
            BigDecimal discount = new BigDecimal("0.50"); // 50% de descuento
            return this.basePrice.multiply(discount).setScale(2, BigDecimal.ROUND_HALF_UP);
        }
    }

    // --- Getters y Setters ---

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBasePrice() {
        return basePrice;
    }

    public void setBasePrice(BigDecimal basePrice) {
        this.basePrice = basePrice;
    }

    public Integer getCapacity() {
        return capacity;
    }

    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }

    public Torneo getTorneo() {
        return torneo;
    }

    public void setTorneo(Torneo torneo) {
        this.torneo = torneo;
    }

    public List<Inscripcion> getInscripciones() {
        return inscripciones;
    }

    public void setInscripciones(List<Inscripcion> inscripciones) {
        this.inscripciones = inscripciones;
    }
}