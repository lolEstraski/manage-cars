package com.example.auth.infrastructure.config;

import com.example.auth.application.services.AuthService;
import com.example.auth.domain.ports.in.AuthUseCase;
import com.example.auth.domain.ports.out.PasswordEncoderPort;
import com.example.auth.domain.ports.out.RoleRepositoryPort;
import com.example.auth.domain.ports.out.TokenPort;
import com.example.auth.domain.ports.out.UserRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public AuthUseCase authUseCase(UserRepositoryPort userRepositoryPort,
                                   RoleRepositoryPort roleRepositoryPort,
                                   PasswordEncoderPort passwordEncoderPort,
                                   TokenPort tokenPort) {
        return new AuthService(userRepositoryPort, roleRepositoryPort, passwordEncoderPort, tokenPort);
    }
}
