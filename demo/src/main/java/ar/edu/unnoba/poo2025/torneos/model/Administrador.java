package ar.edu.unnoba.poo2025.torneos.model;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrimaryKeyJoinColumn;
import jakarta.persistence.Table;

@Entity
@Table(name = "Administrador")
@PrimaryKeyJoinColumn(name = "id")
public class Administrador extends Usuario{
    // No tiene atributos adicionales por ahora
    // Su distinción se da en lo funcional del modelo de negocio, que lo haríamos en capas superiores
    // Tendría además una FK a Usuario que JPA maneja automáticamente con la herencia

    // Relación con Torneo, mapped by es el atributo en la clase torneo para que se haga el @JoinColumn
    // La relación está en memoria, sirve para accesos rápidos y además con lazy evitamos cargas innecesarias, sólo cuando se lo llama
    // Sería el equivalente a relationship en SQLAlchemy

    protected Administrador() {} //constructor vacío protegido para JPA, al ser Protected evita que otros paquetes creen instancias vacías

    public Administrador(String email, String password) {
        super(email, password);
    }
    @OneToMany(mappedBy = "administrador", fetch = FetchType.LAZY)
    private List<Torneo> torneosCreados = new ArrayList<>();
}
