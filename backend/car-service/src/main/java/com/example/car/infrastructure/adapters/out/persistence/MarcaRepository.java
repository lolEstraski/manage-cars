package com.example.car.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MarcaRepository extends JpaRepository<MarcaEntity, Long> {
    Optional<MarcaEntity> findByNombre(String nombre);
}
