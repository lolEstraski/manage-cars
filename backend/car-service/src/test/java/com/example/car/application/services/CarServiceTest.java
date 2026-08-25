package com.example.car.application.services;

import com.example.car.domain.model.Car;
import com.example.car.domain.ports.out.CarRepositoryPort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CarServiceTest {

    @Mock
    private CarRepositoryPort carRepositoryPort;

    @InjectMocks
    private CarService carService;

    private Car mockCar;

    @BeforeEach
    void setUp() {
        mockCar = Car.builder()
                .id(1L)
                .marca("Toyota")
                .modelo("Corolla")
                .anio("2024")
                .placa("ABC-123")
                .color("Rojo")
                .userId(10004L)
                .build();
    }

    @Test
    @DisplayName("Debe guardar un auto correctamente")
    void guardarAuto_Exito() {
        when(carRepositoryPort.guardar(any(Car.class))).thenReturn(mockCar);

        Car result = carService.guardarAuto(mockCar);

        assertNotNull(result);
        assertEquals("Toyota", result.getMarca());
        assertEquals("Corolla", result.getModelo());
        assertEquals(10004L, result.getUserId());
        verify(carRepositoryPort, times(1)).guardar(any(Car.class));
    }

    @Test
    @DisplayName("Debe actualizar los datos de un auto existente")
    void actualizarAuto_Exito() {
        Car autoActualizado = Car.builder()
                .marca("Toyota")
                .modelo("Corolla GR")
                .anio("2025")
                .placa("ABC-123")
                .color("Negro")
                .userId(10002L)
                .imagenUrl("/uploads/corolla.png")
                .build();

        when(carRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(mockCar));
        when(carRepositoryPort.guardar(any(Car.class))).thenReturn(autoActualizado);

        Car result = carService.actualizarAuto(1L, autoActualizado);

        assertNotNull(result);
        assertEquals("Corolla GR", result.getModelo());
        assertEquals("2025", result.getAnio());
        assertEquals("Negro", result.getColor());
        assertEquals(10002L, result.getUserId());
        verify(carRepositoryPort, times(1)).guardar(any(Car.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta actualizar un auto inexistente")
    void actualizarAuto_NoExiste_LanzaExcepcion() {
        when(carRepositoryPort.buscarPorId(999L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> carService.actualizarAuto(999L, mockCar));
        assertTrue(exception.getMessage().contains("no encontrado"));
        verify(carRepositoryPort, never()).guardar(any(Car.class));
    }

    @Test
    @DisplayName("Debe eliminar un auto por ID")
    void eliminarAuto_LlamaARepository() {
        doNothing().when(carRepositoryPort).eliminarPorId(1L);

        carService.eliminarAuto(1L);

        verify(carRepositoryPort, times(1)).eliminarPorId(1L);
    }

    @Test
    @DisplayName("Debe obtener un auto existente por ID")
    void obtenerPorId_Existe() {
        when(carRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(mockCar));

        Car result = carService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals("Corolla", result.getModelo());
    }

    @Test
    @DisplayName("Debe lanzar excepción al buscar por ID inexistente")
    void obtenerPorId_NoExiste() {
        when(carRepositoryPort.buscarPorId(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, 
                () -> carService.obtenerPorId(99L));
        assertEquals("Auto no encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Debe obtener todos los autos paginados")
    void obtenerTodos_Paginado() {
        Page<Car> page = new PageImpl<>(List.of(mockCar));
        when(carRepositoryPort.buscarTodos(0, 10)).thenReturn(page);

        Page<Car> result = carService.obtenerTodos(0, 10);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(carRepositoryPort, times(1)).buscarTodos(0, 10);
    }

    @Test
    @DisplayName("Debe obtener autos por ID de usuario asignado")
    void obtenerPorUsuarioId_Paginado() {
        Page<Car> page = new PageImpl<>(List.of(mockCar));
        when(carRepositoryPort.buscarPorUsuarioId(eq(10004L), eq(0), eq(10))).thenReturn(page);

        Page<Car> result = carService.obtenerPorUsuarioId(10004L, 0, 10);

        assertNotNull(result);
        assertEquals(10004L, result.getContent().get(0).getUserId());
        verify(carRepositoryPort, times(1)).buscarPorUsuarioId(10004L, 0, 10);
    }

    @Test
    @DisplayName("Debe buscar auto por placa")
    void obtenerPorPlaca_Existe() {
        when(carRepositoryPort.buscarPorPlaca("ABC-123")).thenReturn(Optional.of(mockCar));

        Car result = carService.obtenerPorPlaca("ABC-123");

        assertNotNull(result);
        assertEquals("ABC-123", result.getPlaca());
    }

    @Test
    @DisplayName("Debe filtrar autos por marca paginados")
    void obtenerPorMarca_Paginado() {
        Page<Car> page = new PageImpl<>(List.of(mockCar));
        when(carRepositoryPort.buscarPorMarca(eq("Toyota"), eq(0), eq(10))).thenReturn(page);

        Page<Car> result = carService.obtenerPorMarca("Toyota", 0, 10);

        assertNotNull(result);
        assertEquals("Toyota", result.getContent().get(0).getMarca());
    }

    @Test
    @DisplayName("Debe filtrar autos por modelo paginados")
    void obtenerPorModelo_Paginado() {
        Page<Car> page = new PageImpl<>(List.of(mockCar));
        when(carRepositoryPort.buscarPorModelo(eq("Corolla"), eq(0), eq(10))).thenReturn(page);

        Page<Car> result = carService.obtenerPorModelo("Corolla", 0, 10);

        assertNotNull(result);
        assertEquals("Corolla", result.getContent().get(0).getModelo());
    }

    @Test
    @DisplayName("Debe filtrar autos por año paginados")
    void obtenerPorAnio_Paginado() {
        Page<Car> page = new PageImpl<>(List.of(mockCar));
        when(carRepositoryPort.buscarPorAnio(eq("2024"), eq(0), eq(10))).thenReturn(page);

        Page<Car> result = carService.obtenerPorAnio("2024", 0, 10);

        assertNotNull(result);
        assertEquals("2024", result.getContent().get(0).getAnio());
    }

    @Test
    @DisplayName("Debe retornar el conteo total de autos")
    void contarTodos_Exito() {
        when(carRepositoryPort.contar()).thenReturn(150L);

        Long total = carService.contarTodos();

        assertEquals(150L, total);
        verify(carRepositoryPort, times(1)).contar();
    }

    @Test
    @DisplayName("Debe retornar el conteo de autos por usuario")
    void contarPorUsuarioId_Exito() {
        when(carRepositoryPort.contarPorUsuarioId(10004L)).thenReturn(10L);

        Long total = carService.contarPorUsuarioId(10004L);

        assertEquals(10L, total);
        verify(carRepositoryPort, times(1)).contarPorUsuarioId(10004L);
    }
}
