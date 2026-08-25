package com.example.car.infrastructure.config;

import com.example.car.application.services.CarService;
import com.example.car.domain.ports.in.CarUseCase;
import com.example.car.domain.ports.out.CarRepositoryPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CarConfig {

    @Bean
    public CarUseCase carUseCase(CarRepositoryPort carRepositoryPort) {
        return new CarService(carRepositoryPort);
    }
}
