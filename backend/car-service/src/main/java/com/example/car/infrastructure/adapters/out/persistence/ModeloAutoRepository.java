package com.example.car.infrastructure.adapters.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ModeloAutoRepository extends JpaRepository<ModeloAutoEntity, Long> {
    
    @Query("SELECT m FROM ModeloAutoEntity m WHERE m.marca.id = :marcaId")
    List<ModeloAutoEntity> findByMarcaId(@Param("marcaId") Long marcaId);

    @Query("SELECT m FROM ModeloAutoEntity m WHERE m.nombre = :nombre AND m.marca.id = :marcaId")
    Optional<ModeloAutoEntity> findByNombreAndMarcaId(@Param("nombre") String nombre, @Param("marcaId") Long marcaId);
}
