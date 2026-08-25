package com.example.auth.application.services;

import com.example.auth.domain.model.Role;
import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.out.PasswordEncoderPort;
import com.example.auth.domain.ports.out.RoleRepositoryPort;
import com.example.auth.domain.ports.out.TokenPort;
import com.example.auth.domain.ports.out.UserRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
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
    private Role mockRole;

    @BeforeEach
    void setUp() {
        mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setNombre("ROLE_USER");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNombre("Carlos");
        mockUser.setApellido("Gomez");
        mockUser.setCorreo("carlos@test.com");
        mockUser.setContrasena("hashedpass");
        mockUser.setRol(mockRole);
    }

    @Test
    @DisplayName("Debe registrar exitosamente un usuario nuevo con rol ROLE_USER")
    void registrarUsuario_Exito() {
        when(userRepositoryPort.buscarPorCorreo("carlos@test.com")).thenReturn(Optional.empty());
        when(roleRepositoryPort.buscarPorNombre("ROLE_USER")).thenReturn(Optional.of(mockRole));
        when(passwordEncoderPort.encode("password123")).thenReturn("hashedpass");
        when(userRepositoryPort.guardar(any(User.class))).thenReturn(mockUser);

        User reqUser = new User();
        reqUser.setNombre("Carlos");
        reqUser.setApellido("Gomez");
        reqUser.setCorreo("carlos@test.com");
        reqUser.setContrasena("password123");
        
        User result = authService.registrarUsuario(reqUser);

        assertNotNull(result);
        assertEquals("carlos@test.com", result.getCorreo());
        assertEquals("ROLE_USER", result.getRol().getNombre());
        verify(userRepositoryPort, times(1)).guardar(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el correo ya está registrado")
    void registrarUsuario_Fallo_CorreoExiste() {
        when(userRepositoryPort.buscarPorCorreo("carlos@test.com")).thenReturn(Optional.of(mockUser));

        User reqUser = new User();
        reqUser.setCorreo("carlos@test.com");

        RuntimeException exception = assertThrows(RuntimeException.class, () -> authService.registrarUsuario(reqUser));
        assertTrue(exception.getMessage().contains("ya está registrado"));
        verify(userRepositoryPort, never()).guardar(any(User.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si el rol por defecto no existe en la base de datos")
    void registrarUsuario_Fallo_RolNoEncontrado() {
        when(userRepositoryPort.buscarPorCorreo("nuevo@test.com")).thenReturn(Optional.empty());
        when(roleRepositoryPort.buscarPorNombre("ROLE_USER")).thenReturn(Optional.empty());

        User reqUser = new User();
        reqUser.setCorreo("nuevo@test.com");

        assertThrows(RuntimeException.class, () -> authService.registrarUsuario(reqUser));
    }

    @Test
    @DisplayName("Debe iniciar sesión exitosamente y retornar token JWT si credenciales coinciden")
    void iniciarSesion_Exito() {
        when(userRepositoryPort.buscarPorCorreo("carlos@test.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoderPort.matches("password123", "hashedpass")).thenReturn(true);
        when(tokenPort.generarToken(mockUser)).thenReturn("jwt.token.valid");

        String token = authService.iniciarSesion("carlos@test.com", "password123");

        assertNotNull(token);
        assertEquals("jwt.token.valid", token);
    }

    @Test
    @DisplayName("Debe lanzar excepción si el usuario no existe al iniciar sesión")
    void iniciarSesion_Fallo_UsuarioNoExiste() {
        when(userRepositoryPort.buscarPorCorreo("noexiste@test.com")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> authService.iniciarSesion("noexiste@test.com", "password123"));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Debe lanzar excepción si la contraseña no coincide")
    void iniciarSesion_Fallo_ContrasenaIncorrecta() {
        when(userRepositoryPort.buscarPorCorreo("carlos@test.com")).thenReturn(Optional.of(mockUser));
        when(passwordEncoderPort.matches("wrongpass", "hashedpass")).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> authService.iniciarSesion("carlos@test.com", "wrongpass"));
        assertEquals("Credenciales inválidas", exception.getMessage());
    }

    @Test
    @DisplayName("Debe retornar la lista completa de usuarios registrados")
    void obtenerUsuarios_Exito() {
        User user2 = new User();
        user2.setId(2L);
        user2.setNombre("Maria");
        user2.setCorreo("maria@test.com");

        when(userRepositoryPort.buscarTodos()).thenReturn(List.of(mockUser, user2));

        List<User> usuarios = authService.obtenerUsuarios();

        assertNotNull(usuarios);
        assertEquals(2, usuarios.size());
        assertEquals("carlos@test.com", usuarios.get(0).getCorreo());
        assertEquals("maria@test.com", usuarios.get(1).getCorreo());
        verify(userRepositoryPort, times(1)).buscarTodos();
    }
}
