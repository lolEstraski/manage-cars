package com.example.car.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "cars")
public class CarEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String marca;

    @Column(nullable = false)
    private String modelo;

    @Column(nullable = false)
    private String anio;

    @Column(nullable = false, unique = true)
    private String placa;

    @Column(nullable = false)
    private String color;

    @Column
    private String imagenUrl;

    // Solo mantenemos el userId numérico, sin mapeo de llave foránea
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(updatable = false)
    private Date fechaRegistro;
}
