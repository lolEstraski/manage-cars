package com.example.car.domain.ports.out;

import com.example.car.domain.model.Car;
import org.springframework.data.domain.Page;
import java.util.Optional;

public interface CarRepositoryPort {
    Car guardar(Car auto);
    void eliminarPorId(Long id);
    Optional<Car> buscarPorId(Long id);
    
    Page<Car> buscarTodos(int page, int size);
    Optional<Car> buscarPorPlaca(String placa);
    Page<Car> buscarPorAnio(String anio, int page, int size);
    Page<Car> buscarPorMarca(String marca, int page, int size);
    Page<Car> buscarPorModelo(String modelo, int page, int size);
    Page<Car> buscarPorUsuarioId(Long userId, int page, int size);
    
    Long contar();
    Long contarPorUsuarioId(Long userId);
}
