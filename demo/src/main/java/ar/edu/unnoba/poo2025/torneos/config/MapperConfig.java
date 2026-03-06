package ar.edu.unnoba.poo2025.torneos.config;

import ar.edu.unnoba.poo2025.torneos.dto.CreateCompetitionDTO;
import ar.edu.unnoba.poo2025.torneos.model.Competencia;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
public class MapperConfig {

    @Bean
    public ModelMapper modelMapper() {
        ModelMapper mapper = new ModelMapper();

        // Mapeo de CreateCompetitionDTO a Competencia
        mapper.typeMap(CreateCompetitionDTO.class, Competencia.class)
                .addMappings(m -> {
                    m.map(CreateCompetitionDTO::getCupo, Competencia::setCapacity);
                    m.map(CreateCompetitionDTO::getPrecio, Competencia::setBasePrice);
                });

        // Mapeo inverso de Competencia a CreateCompetitionDTO
        mapper.typeMap(Competencia.class, CreateCompetitionDTO.class)
                .addMappings(m -> {
                    m.map(Competencia::getCapacity, CreateCompetitionDTO::setCupo);
                    m.map(Competencia::getBasePrice, CreateCompetitionDTO::setPrecio);
                });

        return mapper;
    }
}
