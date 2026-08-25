package com.example.car.infrastructure.adapters.out.persistence;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarJpaRepository extends JpaRepository<CarEntity, Long> {
    Optional<CarEntity> findByPlaca(String placa);
    Page<CarEntity> findByAnio(String anio, Pageable pageable);
    Page<CarEntity> findByMarca(String marca, Pageable pageable);
    Page<CarEntity> findByModelo(String modelo, Pageable pageable);
    Page<CarEntity> findByUserId(Long userId, Pageable pageable);
    Long countByUserId(Long userId);
}
