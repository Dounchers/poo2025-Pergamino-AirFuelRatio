package ar.edu.unnoba.poo2025.torneos.util;

/*Recomendado utilizar keycloak en lugar de esto */

import com.password4j.Hash;
import com.password4j.Password;

/**
 * Utilidad para encriptar y verificar contraseñas usando Bcrypt.
 * 
 * Bcrypt es un algoritmo de hash seguro que:
 * - Genera un "salt" aleatorio para cada contraseña
 * - Es lento intencionalmente (dificulta ataques de fuerza bruta)
 * - Genera hashes diferentes para la misma contraseña (por el salt)
 * 
 * Ejemplo de uso:
 * - Al crear usuario: passwordEncoder.encode("miPassword123") → "$2a$10$..."
 * - Al hacer login: passwordEncoder.verify("miPassword123", hashGuardado) → true/false
 
 * Usos que provee: 
 * 
 */
public class PasswordEncoder {

    /**
     * Encripta una contraseña en texto plano usando Bcrypt.
     * 
     * @param rawPassword La contraseña en texto plano (ej: "password123")
     * @return El hash Bcrypt de la contraseña (ewj: "$2a$10$...")
     * 
     * Ejemplo:
     * encode("admin123") → "$2a$10$N9qo8uLOickgx2ZMRZoMye..."
     */
    public String encode(String rawPassword) {
        Hash hash = Password.hash(rawPassword).withBcrypt();
        return hash.getResult();
    }

    /**
     * Verifica si una contraseña en texto plano coincide con un hash Bcrypt.
     * 
     * @param rawPassword La contraseña en texto plano a verificar
     * @param encodedPassword El hash Bcrypt guardado en la BD
     * @return true si coinciden, false si no
     * 
     * Ejemplo:
     * verify("admin123", "$2a$10$N9qo8uLO...") → true
     * verify("wrongpass", "$2a$10$N9qo8uLO...") → false
     */
    public boolean verify(String rawPassword, String encodedPassword) {
        return Password.check(rawPassword, encodedPassword).withBcrypt();
    }
}
