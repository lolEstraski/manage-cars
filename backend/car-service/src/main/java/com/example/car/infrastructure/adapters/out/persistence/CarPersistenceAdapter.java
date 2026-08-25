package com.example.car.infrastructure.adapters.out.persistence;

import com.example.car.domain.model.Car;
import com.example.car.domain.ports.out.CarRepositoryPort;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CarPersistenceAdapter implements CarRepositoryPort {

    private final CarJpaRepository carJpaRepository;
    private final ModelMapper modelMapper;

    public CarPersistenceAdapter(CarJpaRepository carJpaRepository, ModelMapper modelMapper) {
        this.carJpaRepository = carJpaRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public Car guardar(Car auto) {
        CarEntity entity = modelMapper.map(auto, CarEntity.class);
        CarEntity saved = carJpaRepository.save(entity);
        return modelMapper.map(saved, Car.class);
    }

    @Override
    public void eliminarPorId(Long id) {
        carJpaRepository.deleteById(id);
    }

    @Override
    public Optional<Car> buscarPorId(Long id) {
        return carJpaRepository.findById(id).map(e -> modelMapper.map(e, Car.class));
    }

    @Override
    public Page<Car> buscarTodos(int page, int size) {
        return carJpaRepository.findAll(PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(e -> modelMapper.map(e, Car.class));
    }

    @Override
    public Optional<Car> buscarPorPlaca(String placa) {
        return carJpaRepository.findByPlaca(placa)
                .map(e -> modelMapper.map(e, Car.class));
    }

    @Override
    public Page<Car> buscarPorAnio(String anio, int page, int size) {
        return carJpaRepository.findByAnio(anio, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(e -> modelMapper.map(e, Car.class));
    }

    @Override
    public Page<Car> buscarPorMarca(String marca, int page, int size) {
        return carJpaRepository.findByMarca(marca, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(e -> modelMapper.map(e, Car.class));
    }

    @Override
    public Page<Car> buscarPorModelo(String modelo, int page, int size) {
        return carJpaRepository.findByModelo(modelo, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(e -> modelMapper.map(e, Car.class));
    }

    @Override
    public Page<Car> buscarPorUsuarioId(Long userId, int page, int size) {
        return carJpaRepository.findByUserId(userId, PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id")))
                .map(e -> modelMapper.map(e, Car.class));
    }

    @Override
    public Long contar() {
        return carJpaRepository.count();
    }

    @Override
    public Long contarPorUsuarioId(Long userId) {
        return carJpaRepository.countByUserId(userId);
    }
}
