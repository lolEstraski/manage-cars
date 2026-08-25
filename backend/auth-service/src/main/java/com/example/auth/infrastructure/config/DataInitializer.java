package com.example.auth.infrastructure.config;

import com.example.auth.domain.model.Role;
import com.example.auth.domain.model.User;
import com.example.auth.domain.ports.out.RoleRepositoryPort;
import com.example.auth.domain.ports.out.UserRepositoryPort;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Date;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initDatabase(RoleRepositoryPort roleRepositoryPort, UserRepositoryPort usuarioRepositoryPort, PasswordEncoder passwordEncoder) {
        return args -> {
            Role userRole = null;
            if (roleRepositoryPort.buscarPorNombre("ROLE_USER").isEmpty()) {
                userRole = new Role();
                userRole.setNombre("ROLE_USER");
                userRole = roleRepositoryPort.guardar(userRole);
            } else {
                userRole = roleRepositoryPort.buscarPorNombre("ROLE_USER").get();
            }
            
            Role adminRole = null;
            if (roleRepositoryPort.buscarPorNombre("ROLE_ADMIN").isEmpty()) {
                adminRole = new Role();
                adminRole.setNombre("ROLE_ADMIN");
                adminRole = roleRepositoryPort.guardar(adminRole);
            } else {
                adminRole = roleRepositoryPort.buscarPorNombre("ROLE_ADMIN").get();
            }

            // 1. Super Administrador
            if (usuarioRepositoryPort.buscarPorCorreo("admin@admin.com").isEmpty()) {
                User adminUser = new User();
                adminUser.setNombre("Super");
                adminUser.setApellido("Administrador");
                adminUser.setCorreo("admin@admin.com");
                adminUser.setContrasena(passwordEncoder.encode("admin123"));
                adminUser.setRol(adminRole);
                adminUser.setFechaCreacion(new Date());
                usuarioRepositoryPort.guardar(adminUser);
            }

            // 2. Usuarios de Prueba
            String[][] testUsers = {
                {"Carlos", "Gomez", "carlos@test.com", "password123"},
                {"Maria", "Rodriguez", "maria@test.com", "password123"},
                {"Juan", "Perez", "juan@test.com", "password123"}
            };

            for (String[] u : testUsers) {
                if (usuarioRepositoryPort.buscarPorCorreo(u[2]).isEmpty()) {
                    User user = new User();
                    user.setNombre(u[0]);
                    user.setApellido(u[1]);
                    user.setCorreo(u[2]);
                    user.setContrasena(passwordEncoder.encode(u[3]));
                    user.setRol(userRole);
                    user.setFechaCreacion(new Date());
                    usuarioRepositoryPort.guardar(user);
                }
            }
        };
    }
}
