package com.example.auth.infrastructure.adapters.in.web;

import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.in.AuthUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthUseCase authUseCase;

    public AuthController(AuthUseCase authUseCase) {
        this.authUseCase = authUseCase;
    }

    @PostMapping("/registro")
    public ResponseEntity<User> registrar(@RequestBody User usuario) {
        try {
            User usuarioRegistrado = authUseCase.registrarUsuario(usuario);
            return ResponseEntity.ok(usuarioRegistrado);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> credenciales) {
        try {
            String token = authUseCase.iniciarSesion(credenciales.get("correo"), credenciales.get("contrasena"));
            return ResponseEntity.ok(Map.of("token", token));
        } catch (Exception e) {
            return ResponseEntity.status(401).body(Map.of("error", e.getMessage()));
        }
    }
}
