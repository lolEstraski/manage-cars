package com.example.auth.domain.ports.out;

import com.example.auth.domain.model.User;
import java.util.Optional;

public interface UserRepositoryPort {
    User guardar(User usuario);
    Optional<User> buscarPorCorreo(String correo);
    Optional<User> buscarPorId(Long id);
}
