package ar.edu.unnoba.poo2025.torneos.dto;

import java.math.BigDecimal;

public class CompetitionResponseDTO {
  private Long id;
  private String name;
  private BigDecimal basePrice; 
  private Integer capacity;
  private String tournamentName;
  private Integer inscriptionsCount;

  public Long getId(){
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

  public String getTournamentName() {
      return tournamentName;
  }

  public void setTournamentName(String tournamentName) {
      this.tournamentName = tournamentName;
  }

  public Integer getInscriptionsCount() {
      return inscriptionsCount;
  }

  public void setInscriptionsCount(Integer inscriptionsCount) {
      this.inscriptionsCount = inscriptionsCount;
  }
}
