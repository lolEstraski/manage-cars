package com.example.car.infrastructure.config;

import com.example.car.infrastructure.adapters.out.persistence.MarcaEntity;
import com.example.car.infrastructure.adapters.out.persistence.MarcaRepository;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoEntity;
import com.example.car.infrastructure.adapters.out.persistence.ModeloAutoRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initCatalogo(MarcaRepository marcaRepository, ModeloAutoRepository modeloAutoRepository) {
        return args -> {
            if (marcaRepository.count() == 0) {
                String[] marcas = {"Toyota", "BMW", "Tesla", "Honda", "Ford", "Chevrolet", "Mercedes-Benz", "Audi", "Nissan", "Hyundai", "Kia", "Volkswagen", "Mazda", "Jeep"};
                String[] modelos = {"Corolla", "X5", "Model 3", "Civic", "Mustang", "Onix", "Clase C", "A4", "Sentra", "Tucson", "Sportage", "Jetta", "CX-5", "Wrangler"};
                
                for (int i = 0; i < marcas.length; i++) {
                    MarcaEntity marca = new MarcaEntity();
                    marca.setNombre(marcas[i]);
                    marca = marcaRepository.save(marca);

                    if (i < modelos.length) {
                        ModeloAutoEntity modelo = new ModeloAutoEntity();
                        modelo.setNombre(modelos[i]);
                        modelo.setMarca(marca);
                        modeloAutoRepository.save(modelo);
                    }
                }
            }
        };
    }
}
