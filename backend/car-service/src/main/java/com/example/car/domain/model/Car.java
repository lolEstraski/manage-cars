package com.example.car.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {
    private Long id;
    private String marca;
    private String modelo;
    private String anio;
    private String placa;
    private String color;
    private String imagenUrl;
    private Long userId;
    private Date fechaRegistro;
}
