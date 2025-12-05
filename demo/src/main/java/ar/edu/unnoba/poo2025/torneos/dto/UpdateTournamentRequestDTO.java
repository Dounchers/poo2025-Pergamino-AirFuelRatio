package ar.edu.unnoba.poo2025.torneos.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class UpdateTournamentRequestDTO {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @NotBlank(message = "La descripcion es obligatoria")
    private String description;
    
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate dateStart;
    

    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate dateEnd;

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

    public void setStartDate(LocalDate startDate) {
        this.dateStart = startDate;
    }

    public LocalDate getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(LocalDate dateEnd) {
        this.dateEnd = dateEnd;
    }
}