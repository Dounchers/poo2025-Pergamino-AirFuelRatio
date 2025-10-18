package ar.edu.unnoba.poo2025.torneos.models;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "torneo") // Mapea a la tabla 'Torneo' del DER
public class Torneo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Corresponde a id: long del UML y INTEGER [PK] del DER

    @Column(name = "name", length = 255, nullable = false)
    private String name; // Corresponde a name: string del UML y VARCHAR(255) NOT NULL del DER

    @Column(name = "description", length = 255, nullable = false)
    private String description; // Corresponde a description: string del UML y VARCHAR(255) NOT NULL del DER

    @Column(name = "date_start", nullable = false)
    private LocalDate dateStart; // Corresponde a dateStart: date del UML y DATE NOT NULL del DER

    @Column(name = "date_end", nullable = false)
    private LocalDate dateEnd; // Corresponde a dateEnd: date del UML y DATE NOT NULL del DER

    @Column(name = "publish", nullable = false)
    private Boolean publish = Boolean.FALSE; // Corresponde a publish: boolean del UML y BOOLEAN NOT NULL DEFAULT FALSE del DER. Inicializado a FALSE según descripción/DER.

    // Relación con Administrador: Torneo (0,n) es administrado por Administrador (1,1)
    // El DER indica administrador_id INTEGER [FK] NOT NULL, por lo tanto, es un ManyToOne obligatorio.
    @ManyToOne(fetch = FetchType.LAZY) // Muchos Torneos pueden ser creados por un Administrador
    @JoinColumn(name = "administrador_id", nullable = false)
    private Administrador administrador;

    // Relación con Competencia: Torneo (1,n) tiene Competencia (0,n)
    // Se mapea con el atributo 'torneo' en la clase Competencia.
    @OneToMany(mappedBy = "torneo", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Competencia> competencias;

    // Relación con Inscripción: No se mapea directamente en Torneo.

    // Constructor vacío (obligatorio para JPA)
    public Torneo() {
    }

    // --- Métodos de Negocio (Operaciones del UML) ---

    // isEditable(): boolean - Del UML: Los administradores podrán editar/eliminar un torneo mientras el mismo no se encuentre publicado.
    public boolean isEditable() {
        return !this.publish;
    }

    // verifyEnrollmentDate(enrollmentDate: Date): boolean - Del UML: La inscripción se puede realizar si la fecha de inscripción es anterior a la fecha de inicio del torneo.
    public boolean verifyEnrollmentDate(LocalDate enrollmentDate) {
        // Asume que el torneo no puede ser nulo y que dateStart ya está cargado.
        return enrollmentDate != null && this.dateStart != null && enrollmentDate.isBefore(this.dateStart);
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart(LocalDate dateStart) {
        this.dateStart = dateStart;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }

    public Boolean getPublish() {
        return publish;
    }

    // Método para publicar/despublicar el torneo
    public void setPublish(Boolean publish) {
        this.publish = publish;
    }

    public Administrador getAdministrador() {
        return administrador;
    }

    public void setAdministrador(Administrador administrador) {
        this.administrador = administrador;
    }

    public List<Competencia> getCompetencias() {
        return competencias;
    }

    public void setCompetencias(List<Competencia> competencias) {
        this.competencias = competencias;
    }
}