package com.example.car.infrastructure.config;

import com.example.car.domain.model.Car;
import com.example.car.domain.ports.in.CarUseCase;
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
    CommandLineRunner initCatalogo(MarcaRepository marcaRepository, ModeloAutoRepository modeloAutoRepository, CarUseCase carUseCase) {
        return args -> {
            String[][] catalogo = {
                {"Toyota", "Corolla", "RAV4", "Hilux", "Yaris", "Camry", "Fortuner"},
                {"BMW", "M3", "M4", "X5", "X6", "Serie 3", "Serie 5"},
                {"Tesla", "Model 3", "Model Y", "Model S", "Model X", "Cybertruck"},
                {"Honda", "Civic", "CR-V", "Accord", "City", "HR-V"},
                {"Ford", "Mustang", "Explorer", "Ranger", "F-150", "Focus"},
                {"Chevrolet", "Onix", "Tracker", "Tahoe", "Camaro", "Cruze", "Captiva"},
                {"Mercedes-Benz", "Clase A", "Clase C", "Clase E", "GLC", "GLE", "AMG GT"},
                {"Audi", "A3", "A4", "A6", "Q3", "Q5", "Q7"},
                {"Nissan", "Sentra", "Versa", "Kicks", "Qashqai", "Frontier", "X-Trail"},
                {"Hyundai", "Tucson", "Santa Fe", "Accent", "Elantra", "Creta"},
                {"Kia", "Sportage", "Seltos", "Rio", "Picanto", "Cerato"},
                {"Volkswagen", "Jetta", "Golf", "Tiguan", "Amarok", "Polo", "Taos"},
                {"Mazda", "Mazda 3", "CX-5", "CX-30", "Mazda 2", "CX-9", "MX-5"},
                {"Jeep", "Wrangler", "Grand Cherokee", "Compass", "Renegade", "Gladiator"}
            };

            for (String[] item : catalogo) {
                String nombreMarca = item[0];
                MarcaEntity marca = marcaRepository.findByNombre(nombreMarca).orElseGet(() -> {
                    MarcaEntity nueva = new MarcaEntity();
                    nueva.setNombre(nombreMarca);
                    return marcaRepository.save(nueva);
                });

                for (int j = 1; j < item.length; j++) {
                    String nombreModelo = item[j];
                    if (modeloAutoRepository.findByNombreAndMarcaId(nombreModelo, marca.getId()).isEmpty()) {
                        ModeloAutoEntity modelo = new ModeloAutoEntity();
                        modelo.setNombre(nombreModelo);
                        modelo.setMarca(marca);
                        modeloAutoRepository.save(modelo);
                    }
                }
            }

            // Si no hay autos, crear una flota inicial distribuida entre los usuarios
            if (carUseCase.contarTodos() == 0) {
                String[][] autosIniciales = {
                    {"Toyota", "Corolla", "2023", "ABC-123A", "Rojo", "1"},
                    {"BMW", "M3", "2024", "BMW-789X", "Azul Metálico", "1"},
                    {"Tesla", "Model 3", "2023", "EV-555T", "Blanco", "2"},
                    {"Ford", "Mustang", "2022", "MUS-444F", "Negro", "2"},
                    {"Chevrolet", "Tracker", "2023", "CHV-222C", "Gris", "3"},
                    {"Mercedes-Benz", "Clase C", "2024", "MBZ-101M", "Plateado", "3"},
                    {"Audi", "A4", "2023", "AUD-303A", "Negro", "4"},
                    {"Mazda", "CX-5", "2022", "MZD-909M", "Rojo Rubí", "4"},
                    {"Nissan", "Frontier", "2021", "NIS-808N", "Blanco", "1"},
                    {"Honda", "Civic", "2023", "HON-707H", "Azul", "2"}
                };

                for (String[] data : autosIniciales) {
                    Car car = Car.builder()
                        .marca(data[0])
                        .modelo(data[1])
                        .anio(data[2])
                        .placa(data[3])
                        .color(data[4])
                        .userId(Long.parseLong(data[5]))
                        .build();
                    carUseCase.guardarAuto(car);
                }
            }
        };
    }
}
