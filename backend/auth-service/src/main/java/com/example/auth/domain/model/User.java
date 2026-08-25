package com.example.auth.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    private Long id;
    private String nombre;
    private String apellido;
    private String correo;
    private String contrasena;
    private Role rol;
    private Date fechaCreacion;
    private Date ultimoAcceso;
}
