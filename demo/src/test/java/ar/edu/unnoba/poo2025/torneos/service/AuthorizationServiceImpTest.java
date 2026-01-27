package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UserNotFoundException;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthorizationServiceImpTest {

    @Mock
    private ParticipantService participantService;
    @Mock
    private JwtTokenUtil jwtTokenUtil;

    @InjectMocks
    private AuthorizationServiceImp authorizationServiceImp;

    //datos de prueba
    private final String TEST_EMAIL = "email@ar";
    private final String TOKEN = "jwt.fake.token";


    @Test
    void shouldNotAuthorizeParticipantWhenTokenIsNotValid() {
        when(jwtTokenUtil.verify(TOKEN)).thenReturn(false);
        assertThrows(InvalidTokenException.class, () -> {
            authorizationServiceImp.authorize(TOKEN);
        });
        // si no llega el token directamente se verifica que no siga el flujo
        verify(participantService, never()).findByEmail(any());
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    void shouldNotAuthorizeParticipantWhenUserNotFound() {
        when(jwtTokenUtil.verify(TOKEN)).thenReturn(true);
        when(jwtTokenUtil.getSubject(TOKEN)).thenReturn(null);

        assertThrows(UserNotFoundException.class, () -> {
            authorizationServiceImp.authorize(TOKEN);
        });

        // se verifica que se llamó a la secuencia hasta encontrar que el usuario no existe
        verify(participantService, times(1)).findByEmail(null);
        verify(jwtTokenUtil, times(1)).getSubject(TOKEN);

        //como el flujo cortó al no encontrar el usuario, no se genera token y verificamos
        verify(jwtTokenUtil, never()).generateToken(any());
    }

    @Test
    void shouldAuthorizeParticipantSuccessfully() throws Exception {

        String email = TEST_EMAIL;

        // Crear un objeto Participante simulado que se encontraría en la DB
        Participante mockedParticipant = new Participante();
        mockedParticipant.setEmail(email);

        // El token es válido
        when(jwtTokenUtil.verify(TOKEN)).thenReturn(true);

        // El token devuelve el email esperado
        when(jwtTokenUtil.getSubject(TOKEN)).thenReturn(email);

        // c) El servicio de participante encuentra al usuario
        when(participantService.findByEmail(email)).thenReturn(mockedParticipant);


        Participante result = authorizationServiceImp.authorize(TOKEN);


        // Verificamos que se devolvió el objeto correcto (el que simulamos encontrar)
        assertNotNull(result, "El participante no debe ser nulo.");
        assertEquals(email, result.getEmail(), "El email del participante debe coincidir con el subject del token.");

        // Verificamos que se llamó a la secuencia completa exactamente una vez
        verify(jwtTokenUtil, times(1)).verify(TOKEN);
        verify(jwtTokenUtil, times(1)).getSubject(TOKEN);
        verify(participantService, times(1)).findByEmail(email);

        // Verificamos que NUNCA se intentó generar un token
        verify(jwtTokenUtil, never()).generateToken(any());
    }
}