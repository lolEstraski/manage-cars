package com.example.auth.domain.ports.in;

import com.example.auth.domain.model.User;

public interface AuthUseCase {
    User registrarUsuario(User usuario);
    String iniciarSesion(String correo, String contrasena);
}
