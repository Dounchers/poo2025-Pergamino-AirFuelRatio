package ar.edu.unnoba.poo2025.torneos.model;

import jakarta.persistence.*;

import java.util.List;
import java.util.ArrayList;


@Entity
@Table( name = "Participante",
        uniqueConstraints = {@UniqueConstraint(columnNames = {"documentType", "document"})},
        indexes = {@Index(name = "idx_participante_document", columnList = "document"),
                @Index(name = "idx_participante_surname", columnList = "surname")
        }
)
@PrimaryKeyJoinColumn(name = "id") // Indica que la PK de Participante se une con el ID de Usuario
public class Participante extends Usuario {

    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String surname;
    @Column(nullable = false, unique = true)
    private String document;
    @Column(nullable = false)
    private String documentType;

    @OneToMany(mappedBy = "participante", fetch = FetchType.LAZY)
    private List<Inscripcion> inscripciones = new ArrayList<>();

    protected Participante() {}

    public Participante(String email, String password, String name, String surname, String document, String documentType) {
        super(email, password);
        this.name = name;
        this.surname = surname;
        this.document = document;
        this.documentType = documentType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getDocumentType() {
        return documentType;
    }

    public void setDocumentType(String documentType) {
        this.documentType = documentType;
    }
}
