package ar.edu.unnoba.poo2025.torneos.dto;

/**
 * DTO para responder con los datos de un administrador.
 * No incluye el password por seguridad.
 */
public class AdminResponseDTO {

    private Long id;
    private String email;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
