package com.example.car.domain.model;

public class ModeloAuto {
    private Long id;
    private String nombre;
    private Long marcaId;

    public ModeloAuto() {}

    public ModeloAuto(Long id, String nombre, Long marcaId) {
        this.id = id;
        this.nombre = nombre;
        this.marcaId = marcaId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Long getMarcaId() { return marcaId; }
    public void setMarcaId(Long marcaId) { this.marcaId = marcaId; }
}
