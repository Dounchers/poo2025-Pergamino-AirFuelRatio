package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.DocumentAlreadyRegisteredException;
import ar.edu.unnoba.poo2025.torneos.exception.EmailAlreadyRegisteredException;
import ar.edu.unnoba.poo2025.torneos.model.Participante;
import ar.edu.unnoba.poo2025.torneos.repository.ParticipantRepository;
import ar.edu.unnoba.poo2025.torneos.util.PasswordEncoder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ParticipantServiceImpTest {

    @Mock
    private ParticipantRepository participantRepository;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    ParticipantServiceImp participantServiceImp;

    private final String EMAIL = "email@email.com";

    @Test
    void shouldReturnEmailAlreadyRegisteredExceptionWhenCreatingParticipantWithExistingEmail() {

        Participante dummyParticipant = new Participante();
        dummyParticipant.setEmail(EMAIL);

        // simulamos que la búsqueda por email devuelve un Participante existente.
        // nos valemos de que el findByEmail solo salta a la excepción si encuentra un participante, por eso se lo pasamos.
        when(participantRepository.findByEmail(EMAIL)).thenReturn(new Participante());

        assertThrows(EmailAlreadyRegisteredException.class, () -> {
            participantServiceImp.create(dummyParticipant);
        }, "Debería fallar si el email ya existe");

        //verificamos
        verify(participantRepository, never()).existsByDocumentTypeAndDocument(any(), any());
        verify(passwordEncoder, never()).encode(any());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void shouldReturnParticipantAlreadyRegisteredException(){
        Participante dummyParticipant = new Participante();
        dummyParticipant.setEmail(EMAIL);
        dummyParticipant.setDocumentType("DNI");
        dummyParticipant.setDocument("12345678");

        // simulamos que la búsqueda por email no encuentra ningún participante.
        when(participantRepository.findByEmail(EMAIL)).thenReturn(null);
        // simulamos que el tipo y número de documento ya están registrados (el repo devuelve true al par de ids).
        when(participantRepository.existsByDocumentTypeAndDocument("DNI", "12345678")).thenReturn(true);

        Exception exception = assertThrows(DocumentAlreadyRegisteredException.class, () -> {
            participantServiceImp.create(dummyParticipant);
        }, "Debería fallar si el tipo y número de documento ya están registrados");

        assertEquals("El tipo y número de documento ya están registrados", exception.getMessage());

        verify(participantRepository, times(1)).findByEmail(EMAIL);
        verify(participantRepository).existsByDocumentTypeAndDocument("DNI", "12345678");
        verify(passwordEncoder, never()).encode(any());
        verify(participantRepository, never()).save(any());
    }

    @Test
    void shouldCreateParticipantSuccessfully() throws Exception {
        Participante dummyParticipant = new Participante();
        dummyParticipant.setEmail(EMAIL);
        dummyParticipant.setPassword("plainPassword");
        dummyParticipant.setDocumentType("DNI");
        dummyParticipant.setDocument("12345678");

        // simulamos que la búsqueda por email no encuentra ningún participante.
        when(participantRepository.findByEmail(EMAIL)).thenReturn(null);
        // simulamos que el tipo y número de documento no están registrados (el repo devuelve false al par de ids).
        when(participantRepository.existsByDocumentTypeAndDocument("DNI", "12345678")).thenReturn(false);
        // simulamos el comportamiento del password encoder.
        when(passwordEncoder.encode("plainPassword")).thenReturn("encodedPassword");

        // Simulamos el guardado, devuelve la instancia de entrada
        when(participantRepository.save(any(Participante.class))).thenReturn(dummyParticipant);

        Participante createdParticipant = participantServiceImp.create(dummyParticipant);

        assertNotNull(createdParticipant, "El participante creado no debería ser nulo");
        assertEquals("encodedPassword", createdParticipant.getPassword(), "La contraseña debería estar cifrada después del save");

        verify(participantRepository, times(1)).findByEmail(EMAIL);
        verify(participantRepository).existsByDocumentTypeAndDocument("DNI", "12345678");
        verify(passwordEncoder, times(1)).encode("plainPassword");
        verify(participantRepository, times(1)).save(dummyParticipant);
    }
}


