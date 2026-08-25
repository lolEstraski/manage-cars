package com.example.auth.application.services;

import com.example.auth.domain.model.Role;
import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.out.PasswordEncoderPort;
import com.example.auth.domain.ports.out.RoleRepositoryPort;
import com.example.auth.domain.ports.out.TokenPort;
import com.example.auth.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepositoryPort userRepositoryPort;
    @Mock
    private RoleRepositoryPort roleRepositoryPort;
    @Mock
    private PasswordEncoderPort passwordEncoderPort;
    @Mock
    private TokenPort tokenPort;

    @InjectMocks
    private AuthService authService;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setNombre("ROLE_USER");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setCorreo("test@test.com");
        mockUser.setContrasena("hashedpass");
        mockUser.setRol(role);
    }

    @Test
    void registrar_Exito() {
        when(userRepositoryPort.buscarPorCorreo("test@test.com")).thenReturn(Optional.empty());
        Role userRole = new Role();
        userRole.setNombre("ROLE_USER");
        when(roleRepositoryPort.buscarPorNombre("ROLE_USER")).thenReturn(Optional.of(userRole));
        when(passwordEncoderPort.encode("pass123")).thenReturn("hashedpass");
        when(userRepositoryPort.guardar(any(User.class))).thenReturn(mockUser);

        User reqUser = new User();
        reqUser.setCorreo("test@test.com");
        reqUser.setContrasena("pass123");
        
        User result = authService.registrarUsuario(reqUser);

        assertNotNull(result);
        assertEquals("test@test.com", result.getCorreo());
        verify(userRepositoryPort, times(1)).guardar(any(User.class));
    }

    @Test
    void registrar_Fallo_CorreoExiste() {
        when(userRepositoryPort.buscarPorCorreo("test@test.com")).thenReturn(Optional.of(mockUser));

        User reqUser = new User();
        reqUser.setCorreo("test@test.com");

        assertThrows(RuntimeException.class, () -> authService.registrarUsuario(reqUser));
    }

    @Test
    void login_Exito() {
        when(userRepositoryPort.buscarPorCorreo("test@test.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoderPort.matches("pass123", "hashedpass")).thenReturn(true);
        when(tokenPort.generarToken(mockUser)).thenReturn("jwt.token.here");

        String token = authService.iniciarSesion("test@test.com", "pass123");

        assertEquals("jwt.token.here", token);
    }
}
