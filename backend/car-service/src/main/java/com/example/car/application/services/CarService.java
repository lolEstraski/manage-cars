package com.example.car.application.services;

import com.example.car.domain.model.Car;
import com.example.car.domain.ports.in.CarUseCase;
import com.example.car.domain.ports.out.CarRepositoryPort;
import org.springframework.data.domain.Page;

public class CarService implements CarUseCase {

    private final CarRepositoryPort carRepositoryPort;

    public CarService(CarRepositoryPort carRepositoryPort) {
        this.carRepositoryPort = carRepositoryPort;
    }

    @Override
    public Car guardarAuto(Car auto) {
        return carRepositoryPort.guardar(auto);
    }

    @Override
    public Car actualizarAuto(Long autoId, Car auto) {
        Car existente = carRepositoryPort.buscarPorId(autoId)
                .orElseThrow(() -> new RuntimeException("Auto no encontrado"));
        
        existente.setMarca(auto.getMarca());
        existente.setModelo(auto.getModelo());
        existente.setAnio(auto.getAnio());
        existente.setPlaca(auto.getPlaca());
        existente.setColor(auto.getColor());
        
        return carRepositoryPort.guardar(existente);
    }

    @Override
    public void eliminarAuto(Long autoId) {
        carRepositoryPort.eliminarPorId(autoId);
    }

    @Override
    public Car obtenerPorId(Long autoId) {
        return carRepositoryPort.buscarPorId(autoId)
                .orElseThrow(() -> new RuntimeException("Auto no encontrado"));
    }

    @Override
    public Page<Car> obtenerTodos(int page, int size) {
        return carRepositoryPort.buscarTodos(page, size);
    }

    @Override
    public Car obtenerPorPlaca(String placa) {
        return carRepositoryPort.buscarPorPlaca(placa)
                .orElseThrow(() -> new RuntimeException("Auto no encontrado con placa: " + placa));
    }

    @Override
    public Page<Car> obtenerPorAnio(String anio, int page, int size) {
        return carRepositoryPort.buscarPorAnio(anio, page, size);
    }

    @Override
    public Page<Car> obtenerPorMarca(String marca, int page, int size) {
        return carRepositoryPort.buscarPorMarca(marca, page, size);
    }

    @Override
    public Page<Car> obtenerPorModelo(String modelo, int page, int size) {
        return carRepositoryPort.buscarPorModelo(modelo, page, size);
    }

    @Override
    public Page<Car> obtenerPorUsuarioId(Long userId, int page, int size) {
        return carRepositoryPort.buscarPorUsuarioId(userId, page, size);
    }

    @Override
    public Long contarTodos() {
        return carRepositoryPort.contar();
    }

    @Override
    public Long contarPorUsuarioId(Long userId) {
        return carRepositoryPort.contarPorUsuarioId(userId);
    }
}
