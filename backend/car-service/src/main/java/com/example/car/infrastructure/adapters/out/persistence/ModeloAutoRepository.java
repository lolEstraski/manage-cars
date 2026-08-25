package com.example.car.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModeloAutoRepository extends JpaRepository<ModeloAutoEntity, Long> {
    List<ModeloAutoEntity> findByMarcaId(Long marcaId);
}
