package ar.edu.unnoba.poo2025.torneos;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import ar.edu.unnoba.poo2025.torneos.util.PasswordEncoder;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;

@SpringBootApplication
public class TorneosApplication {
	public static void main(String[] args) {
		SpringApplication.run(TorneosApplication.class, args);
		System.out.println("Hello, World!");
	}

	/**
	 * Bean de ModelMapper para conversión automática entre Entidades y DTOs.
	 * Al declararlo como @Bean, Spring lo gestiona y permite inyectarlo con @Autowired
	 * en cualquier clase (Services, Controllers, etc.)
	 */
	//LO SACO DE LA CLASE PRINCIPAL PARA DEJARLO EN LA CARPETA CONFIG
	//@Bean
	//public ModelMapper modelMapper() {
	//	return new ModelMapper();
	//}

	/**
	 * Bean de PasswordEncoder para encriptar y verificar contraseñas con Bcrypt.
	 * Al declararlo como @Bean, Spring lo gestiona y permite inyectarlo con @Autowired
	 * en Services (especialmente en ParticipantService para hashear passwords).
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new PasswordEncoder();
	}
	@Bean
	public JwtTokenUtil jwtTokenUtil() {
		return new JwtTokenUtil();
	}
}
