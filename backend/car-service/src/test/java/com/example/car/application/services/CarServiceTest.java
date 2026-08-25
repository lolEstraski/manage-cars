package com.example.car.application.services;

import com.example.car.domain.model.Car;
import com.example.car.domain.ports.out.CarRepositoryPort;
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
                .anio("2020")
                .placa("ABC-123")
                .color("Rojo")
                .userId(1L)
                .build();
    }

    @Test
    void guardarAuto_Exito() {
        when(carRepositoryPort.guardar(any(Car.class))).thenReturn(mockCar);

        Car result = carService.guardarAuto(mockCar);

        assertNotNull(result);
        assertEquals("Toyota", result.getMarca());
        verify(carRepositoryPort, times(1)).guardar(any(Car.class));
    }

    @Test
    void obtenerPorId_Existe() {
        when(carRepositoryPort.buscarPorId(1L)).thenReturn(Optional.of(mockCar));

        Car result = carService.obtenerPorId(1L);

        assertNotNull(result);
        assertEquals("Corolla", result.getModelo());
    }

    @Test
    void eliminarAuto_LlamaARepository() {
        doNothing().when(carRepositoryPort).eliminarPorId(1L);

        carService.eliminarAuto(1L);

        verify(carRepositoryPort, times(1)).eliminarPorId(1L);
    }
}
