package ar.edu.unnoba.poo2025.torneos.dto;

import java.math.BigDecimal;

public class CompetitionDetailDTO {
    private Long id;
    private String name;
    private BigDecimal basePrice;
    private Integer capacity;
    private Long totalInscripciones; // Cantidad de inscriptos
    private BigDecimal montoTotalRecaudado; // Suma de dinero

    // Getters y Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getBasePrice() { return basePrice; }
    public void setBasePrice(BigDecimal basePrice) { this.basePrice = basePrice; }
    public Integer getCapacity() { return capacity; }
    public void setCapacity(Integer capacity) { this.capacity = capacity; }
    public Long getTotalInscripciones() { return totalInscripciones; }
    public void setTotalInscripciones(Long totalInscripciones) { this.totalInscripciones = totalInscripciones; }
    public BigDecimal getMontoTotalRecaudado() { return montoTotalRecaudado; }
    public void setMontoTotalRecaudado(BigDecimal montoTotalRecaudado) { this.montoTotalRecaudado = montoTotalRecaudado; }
}
