package com.example.car.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "marcas")
@Data
public class MarcaEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String nombre;
}
