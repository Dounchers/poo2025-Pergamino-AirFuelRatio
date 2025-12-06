package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.AuthenticationFailedException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;
import ar.edu.unnoba.poo2025.torneos.util.PasswordEncoder;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class) //evita tener que inicializar los mocks manualmente
class AuthenticationServiceImpTest {

    @InjectMocks //le indicamos cuál es la clase a probar, donde se inyectan los mocks
    private AuthenticationServiceImp authenticationServiceImp;

    //los mocks de las dependencias a simular
    @Mock
    private ParticipantService participantService;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    // Datos de prueba
    private final String TEST_EMAIL = "test@unnoba.ar";
    private final String RAW_PASS = "123456";
    private final String HASHED_PASS = "hashed_pass_from_db";
    private final String FAKE_TOKEN = "jwt.fake.token";


    //acá empiezan los escenarios de prueba.
    //patrón AAA (Arrange, Act, Assert) (Ordenar, Actuar, Afirmar)

    @Test
    void shouldReturnUserNotFoundExceptionWhenEmailDoesNotExist(){
        // Arrange
        Participante inputParticipant = new Participante();
        inputParticipant.setEmail(TEST_EMAIL);
        inputParticipant.setPassword(RAW_PASS);

        // Simular que el participante (dado el email) no existe
        when(participantService.findByEmail(TEST_EMAIL)).thenReturn(null);

        // Act & Assert
        assertThrows(UserNotFoundException.class, () -> {
            authenticationServiceImp.authenticate(inputParticipant);
        });

        // verificar que no se intentó verificar la contraseña ni generar token
        verify(passwordEncoder, never()).verify(any(), any());
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    void shouldReturnAuthenticationFailedExceptionWhenPasswordIsIncorrect(){
        // Arrange
        Participante inputParticipant = new Participante();
        inputParticipant.setEmail(TEST_EMAIL);
        inputParticipant.setPassword(RAW_PASS);

        Participante foundParticipant = new Participante();
        foundParticipant.setEmail(TEST_EMAIL);
        foundParticipant.setPassword(HASHED_PASS);

        // Simular que el participante existe
        when(participantService.findByEmail(TEST_EMAIL)).thenReturn(foundParticipant);
        // Simular que la contraseña no coincide
        when(passwordEncoder.verify(RAW_PASS, HASHED_PASS)).thenReturn(false);

        // Act & Assert
        assertThrows(AuthenticationFailedException.class, () -> {
            authenticationServiceImp.authenticate(inputParticipant);
        });

        // verificar que se intentó verificar la contraseña
        verify(passwordEncoder, times(1)).verify(RAW_PASS, HASHED_PASS);
        // verificar que no se intentó generar token
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    void shouldReturnJwtTokenWhenCredentialsAreCorrect() throws Exception {
        // Arrange
        Participante inputParticipant = new Participante();
        inputParticipant.setEmail(TEST_EMAIL);
        inputParticipant.setPassword(RAW_PASS);

        Participante foundParticipant = new Participante();
        foundParticipant.setEmail(TEST_EMAIL);
        foundParticipant.setPassword(HASHED_PASS);

        // Simular que el participante existe
        when(participantService.findByEmail(TEST_EMAIL)).thenReturn(foundParticipant);
        // Simular que la contraseña coincide
        when(passwordEncoder.verify(RAW_PASS, HASHED_PASS)).thenReturn(true);
        // Simular la generación del token JWT
        when(jwtTokenUtil.generateToken(TEST_EMAIL)).thenReturn(FAKE_TOKEN);

        // Act
        String returnedToken = authenticationServiceImp.authenticate(inputParticipant);

        // Assert
        assertEquals(FAKE_TOKEN, returnedToken);

        // verificar que se intentó verificar la contraseña
        verify(passwordEncoder, times(1)).verify(RAW_PASS, HASHED_PASS);
        // verificar que se generó el token
        verify(jwtTokenUtil, times(1)).generateToken(TEST_EMAIL);
    }

}