package com.example.car.domain.ports.in;

import com.example.car.domain.model.Car;
import org.springframework.data.domain.Page;

public interface CarUseCase {
    Car guardarAuto(Car auto);
    Car actualizarAuto(Long autoId, Car auto);
    void eliminarAuto(Long autoId);
    Car obtenerPorId(Long autoId);
    
    Page<Car> obtenerTodos(int page, int size);
    Car obtenerPorPlaca(String placa);
    Page<Car> obtenerPorAnio(String anio, int page, int size);
    Page<Car> obtenerPorMarca(String marca, int page, int size);
    Page<Car> obtenerPorModelo(String modelo, int page, int size);
    Page<Car> obtenerPorUsuarioId(Long userId, int page, int size);
    
    Long contarTodos();
    Long contarPorUsuarioId(Long userId);
}
