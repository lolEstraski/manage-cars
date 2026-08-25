package com.example.auth.domain.ports.out;

import com.example.auth.domain.model.Role;
import java.util.Optional;

public interface RoleRepositoryPort {
    Optional<Role> buscarPorNombre(String nombre);
    Role guardar(Role rol);
}
