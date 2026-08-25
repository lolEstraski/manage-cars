-- --------------------------------------------------------
-- Script de Creación de Base de Datos (SQL Server)
-- Proyecto: MisAutos App
-- --------------------------------------------------------

-- Crear la base de datos (Opcional, descomentar si es necesario)
-- CREATE DATABASE misautos_db;
-- GO
-- USE misautos_db;
-- GO

-- 1. Tabla de Roles
CREATE TABLE roles (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL
);

-- 2. Tabla de Usuarios
CREATE TABLE users (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    apellido VARCHAR(255) NOT NULL,
    correo VARCHAR(255) NOT NULL UNIQUE,
    contrasena VARCHAR(255) NOT NULL,
    fecha_creacion DATETIME2,
    ultimo_acceso DATETIME2,
    role_id BIGINT NOT NULL,
    CONSTRAINT fk_users_roles FOREIGN KEY (role_id) REFERENCES roles(id)
);

-- 3. Tabla de Catálogo: Marcas
CREATE TABLE marcas (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL UNIQUE
);

-- 4. Tabla de Catálogo: Modelos
CREATE TABLE modelos (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    marca_id BIGINT NOT NULL,
    CONSTRAINT fk_modelos_marcas FOREIGN KEY (marca_id) REFERENCES marcas(id)
);

-- 5. Tabla de Autos (Cars)
CREATE TABLE cars (
    id BIGINT IDENTITY(1,1) PRIMARY KEY,
    marca VARCHAR(255) NOT NULL,
    modelo VARCHAR(255) NOT NULL,
    anio VARCHAR(255) NOT NULL,
    placa VARCHAR(255) NOT NULL UNIQUE,
    color VARCHAR(255) NOT NULL,
    imagen_url VARCHAR(255),
    user_id BIGINT NOT NULL, -- Relación lógica con users en auth-service
    fecha_registro DATETIME2 DEFAULT GETDATE()
);

-- --------------------------------------------------------
-- Datos Iniciales
-- --------------------------------------------------------

INSERT INTO roles (nombre) VALUES ('ROLE_USER'), ('ROLE_ADMIN');

-- La contraseña es 'admin123' encriptada en BCrypt
INSERT INTO users (nombre, apellido, correo, contrasena, role_id, fecha_creacion) 
VALUES ('Super', 'Administrador', 'admin@admin.com', '$2a$10$Q7I3z6Q/W8XJ1t7U4p5F4.VpA/T3sL/tZ6I5Z6I5Z6I5Z6I5Z6I5', 2, GETDATE());

INSERT INTO marcas (nombre) VALUES ('Toyota'), ('BMW'), ('Tesla');
