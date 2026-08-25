package com.example.car.infrastructure.adapters.out.persistence;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "modelos")
@Data
public class ModeloAutoEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "marca_id", nullable = false)
    @com.fasterxml.jackson.annotation.JsonIgnore
    private MarcaEntity marca;
}
