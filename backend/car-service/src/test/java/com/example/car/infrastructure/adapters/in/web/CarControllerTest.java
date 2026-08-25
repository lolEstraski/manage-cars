package com.example.car.infrastructure.adapters.in.web;

import com.example.car.domain.model.Car;
import com.example.car.domain.ports.in.CarUseCase;
import com.example.car.infrastructure.adapters.out.persistence.MarcaEntity;
import com.example.car.infrastructure.adapters.out.persistence.MarcaRepository;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoEntity;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CarController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CarControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private CarUseCase carUseCase;

    @MockBean
    private MarcaRepository marcaRepository;

    @MockBean
    private ModeloAutoRepository modeloAutoRepository;

    private Car mockCar;

    @BeforeEach
    void setUp() {
        mockCar = Car.builder()
                .id(1L)
                .marca("Toyota")
                .modelo("Corolla")
                .anio("2024")
                .placa("XYZ-789")
                .color("Rojo")
                .userId(1L)
                .build();
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    @DisplayName("POST /api/v1/autos debe crear un auto y retornar 201 Created")
    void guardarAuto_Exito() throws Exception {
        when(carUseCase.guardarAuto(any(Car.class))).thenReturn(mockCar);

        mockMvc.perform(post("/api/v1/autos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(mockCar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.marca").value("Toyota"))
                .andExpect(jsonPath("$.modelo").value("Corolla"))
                .andExpect(jsonPath("$.placa").value("XYZ-789"));
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    @DisplayName("GET /api/v1/autos/mis-autos debe retornar los autos del usuario")
    void obtenerMisAutos_Exito() throws Exception {
        Page<Car> page = new PageImpl<>(List.of(mockCar));
        when(carUseCase.obtenerPorUsuarioId(eq(1L), anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/autos/mis-autos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].marca").value("Toyota"));
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_ADMIN"})
    @DisplayName("GET /api/v1/autos debe retornar toda la flota al admin")
    void obtenerTodos_Exito() throws Exception {
        Page<Car> page = new PageImpl<>(List.of(mockCar));
        when(carUseCase.obtenerTodos(anyInt(), anyInt())).thenReturn(page);

        mockMvc.perform(get("/api/v1/autos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].modelo").value("Corolla"));
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_ADMIN"})
    @DisplayName("POST /api/v1/autos/precargar debe generar 10 autos")
    void precargarAutos_Exito() throws Exception {
        MarcaEntity marca = new MarcaEntity();
        marca.setId(1L);
        marca.setNombre("Toyota");

        ModeloAutoEntity modelo = new ModeloAutoEntity();
        modelo.setId(1L);
        modelo.setNombre("Corolla");
        modelo.setMarca(marca);

        when(marcaRepository.findAll()).thenReturn(List.of(marca));
        when(modeloAutoRepository.findByMarcaId(1L)).thenReturn(List.of(modelo));
        when(carUseCase.guardarAuto(any(Car.class))).thenReturn(mockCar);

        mockMvc.perform(post("/api/v1/autos/precargar")
                .param("targetUserId", "10004"))
                .andExpect(status().isOk())
                .andExpect(content().string(org.hamcrest.Matchers.containsString("10 autos generados")));

        verify(carUseCase, times(10)).guardarAuto(any(Car.class));
    }

    @Test
    @WithMockUser(username = "1", authorities = {"ROLE_USER"})
    @DisplayName("DELETE /api/v1/autos/{id} debe eliminar el auto y retornar 204 No Content")
    void eliminarAuto_Exito() throws Exception {
        doNothing().when(carUseCase).eliminarAuto(1L);

        mockMvc.perform(delete("/api/v1/autos/1"))
                .andExpect(status().isNoContent());

        verify(carUseCase, times(1)).eliminarAuto(1L);
    }
}
