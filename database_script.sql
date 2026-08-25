-- --------------------------------------------------------
-- Script de Creación y Poblamiento de Base de Datos (SQL Server)
-- Proyecto: MisAutos App
-- --------------------------------------------------------

-- 1. Tabla de Roles
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='roles' and xtype='U')
BEGIN
    CREATE TABLE roles (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(255) NOT NULL
    );
END;

-- 2. Tabla de Usuarios
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='users' and xtype='U')
BEGIN
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
END;

-- 3. Tabla de Catálogo: Marcas
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='marcas' and xtype='U')
BEGIN
    CREATE TABLE marcas (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(255) NOT NULL UNIQUE
    );
END;

-- 4. Tabla de Catálogo: Modelos
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='modelos' and xtype='U')
BEGIN
    CREATE TABLE modelos (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        nombre VARCHAR(255) NOT NULL,
        marca_id BIGINT NOT NULL,
        CONSTRAINT fk_modelos_marcas FOREIGN KEY (marca_id) REFERENCES marcas(id)
    );
END;

-- 5. Tabla de Autos (Cars)
IF NOT EXISTS (SELECT * FROM sysobjects WHERE name='cars' and xtype='U')
BEGIN
    CREATE TABLE cars (
        id BIGINT IDENTITY(1,1) PRIMARY KEY,
        marca VARCHAR(255) NOT NULL,
        modelo VARCHAR(255) NOT NULL,
        anio VARCHAR(255) NOT NULL,
        placa VARCHAR(255) NOT NULL UNIQUE,
        color VARCHAR(255) NOT NULL,
        imagen_url VARCHAR(255),
        user_id BIGINT NOT NULL,
        fecha_registro DATETIME2 DEFAULT GETDATE()
    );
END;

-- --------------------------------------------------------
-- Datos Iniciales
-- --------------------------------------------------------

-- Roles
IF NOT EXISTS (SELECT * FROM roles WHERE nombre = 'ROLE_USER')
    INSERT INTO roles (nombre) VALUES ('ROLE_USER');
IF NOT EXISTS (SELECT * FROM roles WHERE nombre = 'ROLE_ADMIN')
    INSERT INTO roles (nombre) VALUES ('ROLE_ADMIN');

-- Usuarios Iniciales (Contraseña: 'admin123' para Admin, 'password123' para los demás en BCrypt)
IF NOT EXISTS (SELECT * FROM users WHERE correo = 'admin@admin.com')
    INSERT INTO users (nombre, apellido, correo, contrasena, role_id, fecha_creacion) 
    VALUES ('Super', 'Administrador', 'admin@admin.com', '$2a$10$7Z8Vq7w4w7GkE5gM1N7e6.Xm9O0P1Q2R3S4T5U6V7W8X9Y0Z1A2B', (SELECT id FROM roles WHERE nombre='ROLE_ADMIN'), GETDATE());

IF NOT EXISTS (SELECT * FROM users WHERE correo = 'carlos@test.com')
    INSERT INTO users (nombre, apellido, correo, contrasena, role_id, fecha_creacion) 
    VALUES ('Carlos', 'Gomez', 'carlos@test.com', '$2a$10$7Z8Vq7w4w7GkE5gM1N7e6.Xm9O0P1Q2R3S4T5U6V7W8X9Y0Z1A2B', (SELECT id FROM roles WHERE nombre='ROLE_USER'), GETDATE());

IF NOT EXISTS (SELECT * FROM users WHERE correo = 'maria@test.com')
    INSERT INTO users (nombre, apellido, correo, contrasena, role_id, fecha_creacion) 
    VALUES ('Maria', 'Rodriguez', 'maria@test.com', '$2a$10$7Z8Vq7w4w7GkE5gM1N7e6.Xm9O0P1Q2R3S4T5U6V7W8X9Y0Z1A2B', (SELECT id FROM roles WHERE nombre='ROLE_USER'), GETDATE());

IF NOT EXISTS (SELECT * FROM users WHERE correo = 'juan@test.com')
    INSERT INTO users (nombre, apellido, correo, contrasena, role_id, fecha_creacion) 
    VALUES ('Juan', 'Perez', 'juan@test.com', '$2a$10$7Z8Vq7w4w7GkE5gM1N7e6.Xm9O0P1Q2R3S4T5U6V7W8X9Y0Z1A2B', (SELECT id FROM roles WHERE nombre='ROLE_USER'), GETDATE());

-- Catálogo de Marcas y Modelos
-- Toyota
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'Toyota') INSERT INTO marcas (nombre) VALUES ('Toyota');
DECLARE @idToyota BIGINT = (SELECT id FROM marcas WHERE nombre = 'Toyota');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Corolla' AND marca_id = @idToyota) INSERT INTO modelos (nombre, marca_id) VALUES ('Corolla', @idToyota);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'RAV4' AND marca_id = @idToyota) INSERT INTO modelos (nombre, marca_id) VALUES ('RAV4', @idToyota);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Hilux' AND marca_id = @idToyota) INSERT INTO modelos (nombre, marca_id) VALUES ('Hilux', @idToyota);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Yaris' AND marca_id = @idToyota) INSERT INTO modelos (nombre, marca_id) VALUES ('Yaris', @idToyota);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Camry' AND marca_id = @idToyota) INSERT INTO modelos (nombre, marca_id) VALUES ('Camry', @idToyota);

-- BMW
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'BMW') INSERT INTO marcas (nombre) VALUES ('BMW');
DECLARE @idBMW BIGINT = (SELECT id FROM marcas WHERE nombre = 'BMW');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'M3' AND marca_id = @idBMW) INSERT INTO modelos (nombre, marca_id) VALUES ('M3', @idBMW);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'X5' AND marca_id = @idBMW) INSERT INTO modelos (nombre, marca_id) VALUES ('X5', @idBMW);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Serie 3' AND marca_id = @idBMW) INSERT INTO modelos (nombre, marca_id) VALUES ('Serie 3', @idBMW);

-- Tesla
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'Tesla') INSERT INTO marcas (nombre) VALUES ('Tesla');
DECLARE @idTesla BIGINT = (SELECT id FROM marcas WHERE nombre = 'Tesla');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Model 3' AND marca_id = @idTesla) INSERT INTO modelos (nombre, marca_id) VALUES ('Model 3', @idTesla);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Model Y' AND marca_id = @idTesla) INSERT INTO modelos (nombre, marca_id) VALUES ('Model Y', @idTesla);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Cybertruck' AND marca_id = @idTesla) INSERT INTO modelos (nombre, marca_id) VALUES ('Cybertruck', @idTesla);

-- Honda
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'Honda') INSERT INTO marcas (nombre) VALUES ('Honda');
DECLARE @idHonda BIGINT = (SELECT id FROM marcas WHERE nombre = 'Honda');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Civic' AND marca_id = @idHonda) INSERT INTO modelos (nombre, marca_id) VALUES ('Civic', @idHonda);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'CR-V' AND marca_id = @idHonda) INSERT INTO modelos (nombre, marca_id) VALUES ('CR-V', @idHonda);

-- Ford
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'Ford') INSERT INTO marcas (nombre) VALUES ('Ford');
DECLARE @idFord BIGINT = (SELECT id FROM marcas WHERE nombre = 'Ford');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Mustang' AND marca_id = @idFord) INSERT INTO modelos (nombre, marca_id) VALUES ('Mustang', @idFord);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Explorer' AND marca_id = @idFord) INSERT INTO modelos (nombre, marca_id) VALUES ('Explorer', @idFord);

-- Chevrolet
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'Chevrolet') INSERT INTO marcas (nombre) VALUES ('Chevrolet');
DECLARE @idChevrolet BIGINT = (SELECT id FROM marcas WHERE nombre = 'Chevrolet');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Onix' AND marca_id = @idChevrolet) INSERT INTO modelos (nombre, marca_id) VALUES ('Onix', @idChevrolet);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Tracker' AND marca_id = @idChevrolet) INSERT INTO modelos (nombre, marca_id) VALUES ('Tracker', @idChevrolet);

-- Mercedes-Benz
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'Mercedes-Benz') INSERT INTO marcas (nombre) VALUES ('Mercedes-Benz');
DECLARE @idMercedes BIGINT = (SELECT id FROM marcas WHERE nombre = 'Mercedes-Benz');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Clase C' AND marca_id = @idMercedes) INSERT INTO modelos (nombre, marca_id) VALUES ('Clase C', @idMercedes);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'GLC' AND marca_id = @idMercedes) INSERT INTO modelos (nombre, marca_id) VALUES ('GLC', @idMercedes);

-- Audi
IF NOT EXISTS (SELECT * FROM marcas WHERE nombre = 'Audi') INSERT INTO marcas (nombre) VALUES ('Audi');
DECLARE @idAudi BIGINT = (SELECT id FROM marcas WHERE nombre = 'Audi');
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'A4' AND marca_id = @idAudi) INSERT INTO modelos (nombre, marca_id) VALUES ('A4', @idAudi);
IF NOT EXISTS (SELECT * FROM modelos WHERE nombre = 'Q5' AND marca_id = @idAudi) INSERT INTO modelos (nombre, marca_id) VALUES ('Q5', @idAudi);
