package com.example.auth.application.services;

import com.example.auth.domain.model.Role;
import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.in.AuthUseCase;
import com.example.auth.domain.ports.out.PasswordEncoderPort;
import com.example.auth.domain.ports.out.RoleRepositoryPort;
import com.example.auth.domain.ports.out.TokenPort;
import com.example.auth.domain.ports.out.UserRepositoryPort;

import java.util.Optional;

public class AuthService implements AuthUseCase {

    private final UserRepositoryPort userRepositoryPort;
    private final RoleRepositoryPort roleRepositoryPort;
    private final PasswordEncoderPort passwordEncoderPort;
    private final TokenPort tokenPort;

    public AuthService(UserRepositoryPort userRepositoryPort, RoleRepositoryPort roleRepositoryPort, PasswordEncoderPort passwordEncoderPort, TokenPort tokenPort) {
        this.userRepositoryPort = userRepositoryPort;
        this.roleRepositoryPort = roleRepositoryPort;
        this.passwordEncoderPort = passwordEncoderPort;
        this.tokenPort = tokenPort;
    }

    @Override
    public User registrarUsuario(User usuario) {
        if (userRepositoryPort.buscarPorCorreo(usuario.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        Role userRole = roleRepositoryPort.buscarPorNombre("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Rol por defecto no encontrado"));
        
        usuario.setRol(userRole);
        usuario.setContrasena(passwordEncoderPort.encode(usuario.getContrasena()));
        return userRepositoryPort.guardar(usuario);
    }

    @Override
    public String iniciarSesion(String correo, String contrasena) {
        Optional<User> optionalUser = userRepositoryPort.buscarPorCorreo(correo);
        if (optionalUser.isEmpty()) {
            throw new RuntimeException("Credenciales inválidas");
        }

        User usuario = optionalUser.get();
        if (!passwordEncoderPort.matches(contrasena, usuario.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        return tokenPort.generarToken(usuario);
    }

    @Override
    public java.util.List<User> obtenerUsuarios() {
        return userRepositoryPort.buscarTodos();
    }
}
