package com.example.car.infrastructure.adapters.in.web;

import com.example.car.infrastructure.adapters.out.persistence.MarcaEntity;
import com.example.car.infrastructure.adapters.out.persistence.MarcaRepository;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoEntity;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/catalogo")
public class CatalogoController {

    private final MarcaRepository marcaRepository;
    private final ModeloAutoRepository modeloAutoRepository;

    public CatalogoController(MarcaRepository marcaRepository, ModeloAutoRepository modeloAutoRepository) {
        this.marcaRepository = marcaRepository;
        this.modeloAutoRepository = modeloAutoRepository;
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN") || a.getAuthority().equals("ADMIN"));
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
    public ResponseEntity<?> crearMarca(@RequestBody Map<String, Object> body) {
        if (!isAdmin()) {
            return ResponseEntity.status(403).body("Solo los administradores pueden añadir marcas al catálogo.");
        }
        
        String nombre = body.get("nombre") != null ? body.get("nombre").toString().trim() : null;
        if (nombre == null || nombre.isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre de la marca es obligatorio.");
        }

        if (marcaRepository.findByNombre(nombre).isPresent()) {
            return ResponseEntity.badRequest().body("La marca '" + nombre + "' ya se encuentra registrada.");
        }

        MarcaEntity nuevaMarca = new MarcaEntity();
        nuevaMarca.setNombre(nombre);
        return ResponseEntity.ok(marcaRepository.save(nuevaMarca));
    }

    @PostMapping("/modelos")
    public ResponseEntity<?> crearModelo(@RequestBody Map<String, Object> body) {
        if (!isAdmin()) {
            return ResponseEntity.status(403).body("Solo los administradores pueden añadir modelos al catálogo.");
        }
        
        String nombre = body.get("nombre") != null ? body.get("nombre").toString().trim() : null;
        if (nombre == null || nombre.isEmpty()) {
            return ResponseEntity.badRequest().body("El nombre del modelo es obligatorio.");
        }

        Long marcaId = null;
        if (body.get("marcaId") != null) {
            marcaId = Long.parseLong(body.get("marcaId").toString());
        } else if (body.get("marca") != null) {
            Object mObj = body.get("marca");
            if (mObj instanceof Map) {
                Object mId = ((Map<?, ?>) mObj).get("id");
                if (mId != null) {
                    marcaId = Long.parseLong(mId.toString());
                }
            } else {
                marcaId = Long.parseLong(mObj.toString());
            }
        }

        if (marcaId == null) {
            return ResponseEntity.badRequest().body("Debe especificar una marca válida.");
        }

        MarcaEntity marca = marcaRepository.findById(marcaId).orElse(null);
        if (marca == null) {
            return ResponseEntity.badRequest().body("La marca con ID " + marcaId + " no existe.");
        }

        if (modeloAutoRepository.findByNombreAndMarcaId(nombre, marca.getId()).isPresent()) {
            return ResponseEntity.badRequest().body("El modelo '" + nombre + "' ya existe para la marca " + marca.getNombre() + ".");
        }

        ModeloAutoEntity nuevoModelo = new ModeloAutoEntity();
        nuevoModelo.setNombre(nombre);
        nuevoModelo.setMarca(marca);

        ModeloAutoEntity guardado = modeloAutoRepository.save(nuevoModelo);
        return ResponseEntity.ok(guardado);
    }
}
