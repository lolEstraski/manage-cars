package com.example.auth.domain.ports.in;

import com.example.auth.domain.model.User;
import java.util.List;

public interface AuthUseCase {
    User registrarUsuario(User usuario);
    String iniciarSesion(String correo, String contrasena);
    List<User> obtenerUsuarios();
}

