package com.example.car.infrastructure.adapters.in.web;

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
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CatalogoController.class)
@AutoConfigureMockMvc(addFilters = false)
public class CatalogoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MarcaRepository marcaRepository;

    @MockBean
    private ModeloAutoRepository modeloAutoRepository;

    private MarcaEntity mockMarca;
    private ModeloAutoEntity mockModelo;

    @BeforeEach
    void setUp() {
        mockMarca = new MarcaEntity();
        mockMarca.setId(1L);
        mockMarca.setNombre("Toyota");

        mockModelo = new ModeloAutoEntity();
        mockModelo.setId(1L);
        mockModelo.setNombre("Corolla");
        mockModelo.setMarca(mockMarca);
    }

    @Test
    @DisplayName("GET /api/v1/catalogo/marcas debe retornar la lista de marcas")
    void obtenerMarcas_Exito() throws Exception {
        when(marcaRepository.findAll()).thenReturn(List.of(mockMarca));

        mockMvc.perform(get("/api/v1/catalogo/marcas")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Toyota"))
                .andExpect(jsonPath("$[0].id").value(1));
    }

    @Test
    @DisplayName("GET /api/v1/catalogo/marcas/{id}/modelos debe retornar los modelos de la marca")
    void obtenerModelosPorMarca_Exito() throws Exception {
        when(modeloAutoRepository.findByMarcaId(1L)).thenReturn(List.of(mockModelo));

        mockMvc.perform(get("/api/v1/catalogo/marcas/1/modelos")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Corolla"))
                .andExpect(jsonPath("$[0].marcaId").value(1));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    @DisplayName("POST /api/v1/catalogo/marcas debe crear una nueva marca si es admin")
    void crearMarca_Exito() throws Exception {
        when(marcaRepository.findByNombre("Porsche")).thenReturn(Optional.empty());
        
        MarcaEntity porsche = new MarcaEntity();
        porsche.setId(2L);
        porsche.setNombre("Porsche");
        when(marcaRepository.save(any(MarcaEntity.class))).thenReturn(porsche);

        mockMvc.perform(post("/api/v1/catalogo/marcas")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nombre", "Porsche"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Porsche"))
                .andExpect(jsonPath("$.id").value(2));
    }

    @Test
    @WithMockUser(authorities = {"ROLE_ADMIN"})
    @DisplayName("POST /api/v1/catalogo/modelos debe crear un nuevo modelo asociado a marca existente")
    void crearModelo_Exito() throws Exception {
        when(marcaRepository.findById(1L)).thenReturn(Optional.of(mockMarca));
        when(modeloAutoRepository.findByNombreAndMarcaId(eq("Yaris Cross"), eq(1L))).thenReturn(Optional.empty());

        ModeloAutoEntity yaris = new ModeloAutoEntity();
        yaris.setId(10L);
        yaris.setNombre("Yaris Cross");
        yaris.setMarca(mockMarca);
        when(modeloAutoRepository.save(any(ModeloAutoEntity.class))).thenReturn(yaris);

        mockMvc.perform(post("/api/v1/catalogo/modelos")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(Map.of("nombre", "Yaris Cross", "marcaId", 1))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Yaris Cross"))
                .andExpect(jsonPath("$.marcaId").value(1));
    }
}
