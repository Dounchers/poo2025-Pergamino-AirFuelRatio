package ar.edu.unnoba.poo2025.torneos.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class CreateTournamentRequestDTO {
    
    @NotBlank(message = "El nombre no puede estar vacío")
    private String name;

    @NotBlank(message = "La descripción no puede estar vacía")
    private String description;

    @NotNull(message = "La fecha de inicio no puede estar vacía")
    private LocalDate dateStart;
    
    @NotNull(message = "La fecha de fin no puede estar vacía")
    private LocalDate dateEnd;

    public String getName() {
        return name;
    }

    public void setName (String name) {
        this.name = name;
    }
    
    public String getDescription() {
        return description;
    }

    public void setDescription (String description) {
        this.description = description;
    }
    public LocalDate getDateStart() {
        return dateStart;
    }

    public void setDateStart (LocalDate dateStart) {
        this.dateStart = dateStart;
    }   

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }
}