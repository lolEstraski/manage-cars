package com.example.car.infrastructure.adapters.out.persistence;

import com.fasterxml.jackson.annotation.JsonProperty;
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

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "marca_id", nullable = false)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private MarcaEntity marca;

    @JsonProperty("marcaId")
    public Long getMarcaId() {
        return marca != null ? marca.getId() : null;
    }
}
