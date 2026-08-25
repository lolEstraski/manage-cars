package com.example.auth.infrastructure.adapters.in.web;

import com.example.auth.domain.model.Role;
import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.in.AuthUseCase;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AuthController.class)
@AutoConfigureMockMvc(addFilters = false)
public class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthUseCase authUseCase;

    private User mockUser;

    @BeforeEach
    void setUp() {
        Role role = new Role();
        role.setId(1L);
        role.setNombre("ROLE_USER");

        mockUser = new User();
        mockUser.setId(1L);
        mockUser.setNombre("Carlos");
        mockUser.setApellido("Gomez");
        mockUser.setCorreo("carlos@test.com");
        mockUser.setRol(role);
    }

    @Test
    @DisplayName("POST /api/v1/auth/registro debe retornar 200 OK al registrar un usuario")
    void registrar_Exito() throws Exception {
        when(authUseCase.registrarUsuario(any(User.class))).thenReturn(mockUser);

        mockMvc.perform(post("/api/v1/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUser)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.correo").value("carlos@test.com"))
                .andExpect(jsonPath("$.nombre").value("Carlos"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/registro debe retornar 400 Bad Request si ocurre error")
    void registrar_Fallo() throws Exception {
        when(authUseCase.registrarUsuario(any(User.class))).thenThrow(new RuntimeException("El correo ya existe"));

        mockMvc.perform(post("/api/v1/auth/registro")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockUser)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 200 OK con el token JWT")
    void login_Exito() throws Exception {
        Map<String, String> loginRequest = Map.of("correo", "carlos@test.com", "contrasena", "password123");
        when(authUseCase.iniciarSesion(eq("carlos@test.com"), eq("password123"))).thenReturn("jwt.token.valid");

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token.valid"));
    }

    @Test
    @DisplayName("POST /api/v1/auth/login debe retornar 401 Unauthorized si credenciales son incorrectas")
    void login_Fallo_CredencialesInvalidas() throws Exception {
        Map<String, String> loginRequest = Map.of("correo", "carlos@test.com", "contrasena", "wrongpass");
        when(authUseCase.iniciarSesion(eq("carlos@test.com"), eq("wrongpass"))).thenThrow(new RuntimeException("Contraseña incorrecta"));

        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.error").value("Contraseña incorrecta"));
    }

    @Test
    @DisplayName("GET /api/v1/auth/usuarios debe retornar 200 OK con la lista de usuarios")
    void obtenerUsuarios_Exito() throws Exception {
        when(authUseCase.obtenerUsuarios()).thenReturn(List.of(mockUser));

        mockMvc.perform(get("/api/v1/auth/usuarios")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].correo").value("carlos@test.com"))
                .andExpect(jsonPath("$[0].id").value(1));
    }
}
