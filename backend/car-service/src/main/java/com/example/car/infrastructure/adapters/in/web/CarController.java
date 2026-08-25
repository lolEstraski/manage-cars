package com.example.car.infrastructure.adapters.in.web;

import com.example.car.domain.model.Car;
import com.example.car.domain.ports.in.CarUseCase;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/autos")
public class CarController {

    private final CarUseCase carUseCase;
    private final com.example.car.infrastructure.adapters.out.persistence.MarcaRepository marcaRepository;
    private final com.example.car.infrastructure.adapters.out.persistence.ModeloAutoRepository modeloAutoRepository;

    public CarController(CarUseCase carUseCase, 
                         com.example.car.infrastructure.adapters.out.persistence.MarcaRepository marcaRepository,
                         com.example.car.infrastructure.adapters.out.persistence.ModeloAutoRepository modeloAutoRepository) {
        this.carUseCase = carUseCase;
        this.marcaRepository = marcaRepository;
        this.modeloAutoRepository = modeloAutoRepository;
    }

    private Long getAuthenticatedUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return Long.parseLong(authentication.getName());
    }

    private boolean isAdmin() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null && authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }

    @PostMapping
    public ResponseEntity<Car> guardarAuto(@RequestBody Car auto) {
        if (!isAdmin() || auto.getUserId() == null) {
            auto.setUserId(getAuthenticatedUserId());
        }
        Car guardado = carUseCase.guardarAuto(auto);
        URI location = URI.create("/api/v1/autos/" + guardado.getId());
        return ResponseEntity.created(location).body(guardado);
    }

    @PutMapping("/{autoId}")
    public ResponseEntity<Car> actualizarAuto(@PathVariable Long autoId, @RequestBody Car auto) {
        return ResponseEntity.ok(carUseCase.actualizarAuto(autoId, auto));
    }

    @DeleteMapping("/{autoId}")
    public ResponseEntity<Void> eliminarAuto(@PathVariable Long autoId) {
        carUseCase.eliminarAuto(autoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{autoId}")
    public ResponseEntity<Car> obtenerPorId(@PathVariable Long autoId) {
        return ResponseEntity.ok(carUseCase.obtenerPorId(autoId));
    }

    @GetMapping
    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    public ResponseEntity<Page<Car>> obtenerTodos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "500") int size) {
        return ResponseEntity.ok(carUseCase.obtenerTodos(page, size));
    }

    @PostMapping("/precargar")
    public ResponseEntity<String> precargarAutos(@RequestParam(required = false) Long targetUserId) {
        Long userId = getAuthenticatedUserId();
        if (isAdmin() && targetUserId != null) {
            userId = targetUserId;
        }
        
        java.util.List<com.example.car.infrastructure.adapters.out.persistence.MarcaEntity> marcasDb = marcaRepository.findAll();
        if (marcasDb.isEmpty()) {
            return ResponseEntity.badRequest().body("No hay marcas en el catálogo para generar autos.");
        }

        String[] colores = {"Rojo", "Azul", "Negro", "Blanco", "Gris", "Plateado", "Verde", "Azul Marino", "Amarillo"};
        String[] prefijos = {"RDM", "AUT", "CAR", "VEH", "COL", "BGT", "MED", "CAL"};
        
        for (int i = 0; i < 10; i++) {
            com.example.car.infrastructure.adapters.out.persistence.MarcaEntity marcaAleatoria = marcasDb.get((int) (Math.random() * marcasDb.size()));
            java.util.List<com.example.car.infrastructure.adapters.out.persistence.ModeloAutoEntity> modelosDb = modeloAutoRepository.findByMarcaId(marcaAleatoria.getId());
            
            String modeloNombre = modelosDb.isEmpty() ? "Genérico" : modelosDb.get((int) (Math.random() * modelosDb.size())).getNombre();
            int rColor = (int) (Math.random() * colores.length);
            String prefijo = prefijos[(int) (Math.random() * prefijos.length)];
            int numAleatorio = 100 + (int)(Math.random() * 899);
            char letraAleatoria = (char)('A' + (int)(Math.random() * 26));
            String placa = prefijo + "-" + numAleatorio + letraAleatoria;
            
            Car auto = Car.builder()
                .marca(marcaAleatoria.getNombre())
                .modelo(modeloNombre)
                .anio(String.valueOf(2016 + (int)(Math.random() * 9)))
                .placa(placa)
                .color(colores[rColor])
                .userId(userId)
                .build();
            carUseCase.guardarAuto(auto);
        }
        return ResponseEntity.ok("10 autos generados correctamente para el usuario ID " + userId);
    }

    @PostMapping("/{autoId}/imagen")
    public ResponseEntity<Car> subirImagen(@PathVariable Long autoId, @RequestParam("file") org.springframework.web.multipart.MultipartFile file) {
        Car auto = carUseCase.obtenerPorId(autoId);
        if (auto == null) {
            return ResponseEntity.notFound().build();
        }
        
        try {
            java.io.File uploadsDir = new java.io.File("uploads");
            if (!uploadsDir.exists()) {
                uploadsDir.mkdir();
            }
            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            java.nio.file.Path path = java.nio.file.Paths.get("uploads/" + fileName);
            java.nio.file.Files.write(path, file.getBytes());
            
            auto.setImagenUrl("/uploads/" + fileName);
            carUseCase.actualizarAuto(autoId, auto);
            return ResponseEntity.ok(auto);
        } catch (java.io.IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/placa/{placa}")
    public ResponseEntity<Car> obtenerPorPlaca(@PathVariable String placa) {
        return ResponseEntity.ok(carUseCase.obtenerPorPlaca(placa));
    }

    @GetMapping("/anio/{anio}")
    public ResponseEntity<Page<Car>> obtenerPorAnio(@PathVariable String anio, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "500") int size) {
        return ResponseEntity.ok(carUseCase.obtenerPorAnio(anio, page, size));
    }

    @GetMapping("/marca/{marca}")
    public ResponseEntity<Page<Car>> obtenerPorMarca(@PathVariable String marca, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "500") int size) {
        return ResponseEntity.ok(carUseCase.obtenerPorMarca(marca, page, size));
    }

    @GetMapping("/modelo/{modelo}")
    public ResponseEntity<Page<Car>> obtenerPorModelo(@PathVariable String modelo, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "500") int size) {
        return ResponseEntity.ok(carUseCase.obtenerPorModelo(modelo, page, size));
    }

    @GetMapping("/usuario/{userId}")
    public ResponseEntity<Page<Car>> obtenerPorUsuarioId(@PathVariable Long userId, @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "500") int size) {
        return ResponseEntity.ok(carUseCase.obtenerPorUsuarioId(userId, page, size));
    }

    @GetMapping("/mis-autos")
    public ResponseEntity<Page<Car>> obtenerMisAutos(@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "500") int size) {
        return ResponseEntity.ok(carUseCase.obtenerPorUsuarioId(getAuthenticatedUserId(), page, size));
    }

    @GetMapping("/conteo")
    public ResponseEntity<Long> contarTodos() {
        return ResponseEntity.ok(carUseCase.contarTodos());
    }

    @GetMapping("/conteo/usuario/{userId}")
    public ResponseEntity<Long> contarPorUsuarioId(@PathVariable Long userId) {
        return ResponseEntity.ok(carUseCase.contarPorUsuarioId(userId));
    }
}
