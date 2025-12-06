package ar.edu.unnoba.poo2025.torneos.service;

import ar.edu.unnoba.poo2025.torneos.exception.InvalidTokenException;
import ar.edu.unnoba.poo2025.torneos.exception.UnauthorizedException;
import ar.edu.unnoba.poo2025.torneos.model.Administrador;
import ar.edu.unnoba.poo2025.torneos.repository.AdminRepository;
import ar.edu.unnoba.poo2025.torneos.util.JwtTokenUtil;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AdminAuthorizationServiceTest {

    @Mock
    private JwtTokenUtil jwtTokenUtil;
    @Mock
    private AdminRepository adminRepository;

    @InjectMocks
    private AdminAuthorizationService adminAuthorizationService;

    @Test
    void shouldReturnInvalidTokenExceptionWhenTokenIsInvalid() {
        String token = "invalidToken";
        //simulamos que el jwt verify nos retornara false
        when(jwtTokenUtil.verify(token)).thenReturn(false);

        assertThrows(InvalidTokenException.class, () -> {
            adminAuthorizationService.authorize(token);
        }, "Debería lanzar InvalidTokenException al no verificar el token");

        verify(jwtTokenUtil, times(1)).verify(token);
        verify(adminRepository, never()).findByEmail(any());
    }

    @Test
    void shouldReturnUnauthorizedExceptionWhenUserIsNotAdmin() {
        String token = "validToken";
        String user_email = "invalid@gmail.ar";

        when(jwtTokenUtil.verify(token)).thenReturn(true);
        when(jwtTokenUtil.getSubject(token)).thenReturn(user_email);
        //simulamos que buscamos el user_email en la tabla de admins y no lo encontramos
        when(adminRepository.findByEmail(user_email)).thenReturn(null);

        assertThrows(UnauthorizedException.class, () -> {
            adminAuthorizationService.authorize(token);
        }, "Debería lanzar UnauthorizedException si el usuario no es admin");

        verify(jwtTokenUtil, times(1)).verify(token);
        verify(jwtTokenUtil, times(1)).getSubject(token);
        verify(adminRepository, times(1)).findByEmail(user_email);

    }



    @Test
    void shouldAuthorizeSuccessfully() {
        Administrador dummyAdmin = new Administrador();

        String token = "validToken";
        String admin_email = "admin@unnoba.ar";

        when(jwtTokenUtil.verify(token)).thenReturn(true);
        when(jwtTokenUtil.getSubject(token)).thenReturn(admin_email);
        when(adminRepository.findByEmail(admin_email)).thenReturn(dummyAdmin);

        assertDoesNotThrow(() -> {
            adminAuthorizationService.authorize(token);
        }, "No debería lanzar excepción si el token es válido y el usuario es admin");

        verify(jwtTokenUtil, times(1)).verify(token);
        verify(jwtTokenUtil, times(1)).getSubject(token);
        verify(adminRepository, times(1)).findByEmail(admin_email);

    }

}