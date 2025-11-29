package ar.edu.unnoba.poo2025.torneos.dto;

import java.math.BigDecimal;

public class CreateCompetitionDTO {
    private String name;
    private Integer cupo;
    private BigDecimal precio;

    // Getters y Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public Integer getCupo() { return cupo; }
    public void setCupo(Integer cupo) { this.cupo = cupo; }
    public BigDecimal getPrecio() { return precio; }
    public void setPrecio(BigDecimal precio) { this.precio = precio; }
}
