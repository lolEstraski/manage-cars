package com.example.car.infrastructure.adapters.in.web;

import com.example.car.infrastructure.adapters.out.persistence.MarcaEntity;
import com.example.car.infrastructure.adapters.out.persistence.MarcaRepository;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoEntity;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {

    private final MarcaRepository marcaRepository;
    private final ModeloAutoRepository modeloAutoRepository;

    public CatalogoController(MarcaRepository marcaRepository, ModeloAutoRepository modeloAutoRepository) {
        this.marcaRepository = marcaRepository;
        this.modeloAutoRepository = modeloAutoRepository;
    }

    @GetMapping("/marcas")
    public ResponseEntity<List<MarcaEntity>> obtenerMarcas() {
        return ResponseEntity.ok(marcaRepository.findAll());
    }

    @GetMapping("/marcas/{marcaId}/modelos")
    public ResponseEntity<List<ModeloAutoEntity>> obtenerModelosPorMarca(@PathVariable Long marcaId) {
        return ResponseEntity.ok(modeloAutoRepository.findByMarcaId(marcaId));
    }

    @PostMapping("/marcas")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<MarcaEntity> crearMarca(@RequestBody MarcaEntity marca) {
        return ResponseEntity.ok(marcaRepository.save(marca));
    }

    @PostMapping("/modelos")
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<ModeloAutoEntity> crearModelo(@RequestBody ModeloAutoEntity modelo) {
        // En una app real, validad que marca.id existe, aquí simplificado
        return ResponseEntity.ok(modeloAutoRepository.save(modelo));
    }
}
