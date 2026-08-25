#  Manage Cars - Sistema de Gestión de Flotas y Vehículos

Sistema full-stack para la administración, registro y control de vehículos basado en una **arquitectura de microservicios** desacoplada, siguiendo los principios de la **Arquitectura Hexagonal (Puertos y Adaptadores)** y una interfaz moderna con diseño *Glassmorphism Dark*.

---

## Tecnologías Utilizadas

### **Backend (Microservicios)**
- **Lenguaje**: Java 17 (Eclipse Temurin)
- **Framework**: Spring Boot 3.5
- **Seguridad**: Spring Security + JSON Web Tokens (JWT)
- **Persistencia**: Spring Data JPA / Hibernate
- **Base de Datos**: Microsoft SQL Server 2022
- **Arquitectura**: Hexagonal (Domain, Application Ports/Services, Infrastructure Adapters)

### **Frontend**
- **Framework**: Angular 19 (Standalone Components)
- **Lenguaje**: TypeScript
- **Estilos**: Vanilla CSS moderno con tokens de diseño, animaciones y soporte responsive
- **Servidor Web Producción**: Nginx Alpine

### **DevOps & Herramientas**
- **Contenedores**: Docker & Docker Compose (Multi-stage builds)
- **API Testing**: Postman Collection v2.2.0 incluida (`ManageCars_Postman_Collection.json`)
- **Control de Versiones**: Git & GitHub

---

##  Funcionalidades Principales

1. ** Autenticación y Control de Accesos (RBAC)**:
   - Registro e inicio de sesión con tokens JWT.
   - Roles diferenciados: `ROLE_ADMIN` (Administrador) y `ROLE_USER` (Usuario Estándar).
2. ** Gestión de Vehículos**:
   - **Mi Garaje**: Los usuarios estándar administran sus propios vehículos.
   - **Panel de Flota (Admin)**: El administrador visualiza todos los autos, con ordenamiento del más reciente al más antiguo.
   - **Asignación a Usuarios**: El administrador puede registrar o generar autos y asignarlos directamente a cualquier usuario del sistema.
   - ** Generador de Lote (10 Autos)**: Genera 10 autos aleatorios con marcas, modelos, placas únicas y colores surtidos vinculados al usuario elegido.
   - **Carga de Imágenes**: Soporte para subir fotografías de los vehículos.
3. **Catálogo de Marcas y Modelos**:
   - Catálogo precargado con 14 marcas (Toyota, BMW, Mercedes-Benz, Audi, Mazda, Tesla, Volvo, etc.) y decenas de modelos.
   - Gestión en tiempo real para crear nuevas marcas y asociarles nuevos modelos.
4. **Filtros en Tiempo Real**:
   - Búsqueda instantánea por placa, modelo, marca, año y usuario propietario.

---

## 1. Cómo Ejecutar con Docker (Recomendado)

La forma más rápida y sencilla de levantar toda la plataforma (Base de Datos + Microservicios + Frontend) es usando Docker Compose:

### **Paso 1: Clonar el repositorio**
```bash
git clone https://github.com/lolEstraski/manage-cars.git
cd manage-cars
```

### **Paso 2: Construir e iniciar todos los contenedores**
```bash
docker compose up --build -d
```

### **Paso 3: Verificar que todos los servicios estén corriendo**
```bash
docker compose ps
```

Deberás ver los 4 contenedores activos:
- **Frontend**: [http://localhost:4200](http://localhost:4200)
- **Auth Service**: `http://localhost:8085`
- **Car Service**: `http://localhost:8081`
- **SQL Server**: `localhost:1433`

### **Comandos Útiles de Docker:**
- **Ver logs en tiempo real**:
  ```bash
  docker compose logs -f
  ```
- **Detener los servicios**:
  ```bash
  docker compose down
  ```
- **Reiniciar y reconstruir un servicio específico**:
  ```bash
  docker compose up --build -d frontend
  docker compose up --build -d car-service
  ```

---

## 2. Cómo Ejecutar en Local (Desarrollo)

Si prefieres ejecutar cada servicio directamente en tu máquina:

### **Requisitos Previos:**
- Java 17 JDK instalado y configurado (`JAVA_HOME`).
- Node.js 18+ y npm instalados.
- Microsoft SQL Server 2022 corriendo en `localhost:1433` (con usuario `sa` y contraseña `YourStrong@Passw0rd`).

> **Consejo**: Puedes levantar únicamente la base de datos con Docker ejecutando:
> ```bash
> docker compose up -d sqlserver db-init
> ```

---

### **Paso 1: Iniciar `auth-service`**
Abre una terminal y navega al directorio del servicio:
```bash
cd backend/auth-service
# En Windows:
.\mvnw.cmd spring-boot:run
# En Linux/Mac:
./mvnw spring-boot:run
```
*El servicio iniciará en el puerto **8085** (mapeado para comunicación).*

---

### **Paso 2: Iniciar `car-service`**
Abre una segunda terminal:
```bash
cd backend/car-service
# En Windows:
.\mvnw.cmd spring-boot:run
# En Linux/Mac:
./mvnw spring-boot:run
```
*El servicio iniciará en el puerto **8081**.*

---

### **Paso 3: Iniciar el Frontend (Angular)**
Abre una tercera terminal:
```bash
cd frontend/manage-cars-front
npm install
npm start
```
*La aplicación web estará disponible en [http://localhost:4200](http://localhost:4200).*

---

##  Cuentas de Prueba Preconfiguradas

La base de datos incluye los siguientes usuarios de prueba listos para usar:

| Rol | Correo | Contraseña | Descripción |
| :--- | :--- | :--- | :--- |
| **Administrador** | `admin@admin.com` | `admin123` | Control total, asignación de flota, generación de 10 autos y catálogo |
| **Usuario** | `carlos@test.com` | `password123` | Garaje personal de Carlos Gomez |
| **Usuario** | `maria@test.com` | `password123` | Garaje personal de Maria Rodriguez |
| **Usuario** | `juan@test.com` | `password123` | Garaje personal de Juan Perez |

---

##  Pruebas de API con Postman

El repositorio incluye la colección completa para Postman en el archivo:
📄 **`ManageCars_Postman_Collection.json`**

### **Instrucciones:**
1. Abre Postman y haz clic en **Import**.
2. Selecciona el archivo `ManageCars_Postman_Collection.json`.
3. Ejecuta la petición **`1. Auth Service -> 2. Login Administrador`**:
   - Guarda automáticamente el token JWT en la variable `{{jwt_token}}`.
4. Todas las peticiones restantes (CRUD de autos, generación de 10 autos, filtros y catálogo) funcionarán de forma inmediata y autenticada.

---

## 3. Ejecución de Pruebas Automatizadas (Testing Suite)

La plataforma cuenta con una suite completa de **pruebas unitarias y de integración** para los servicios del backend y frontend:

### **A. Pruebas de `auth-service` (12 Tests - 100% Passing)**
Valida el registro, login seguro, manejo de roles, excepciones de credenciales y listado de usuarios:
```bash
cd backend/auth-service
# En Windows:
.\mvnw.cmd test
# En Linux/Mac:
./mvnw test
```

### **B. Pruebas de `car-service` (23 Tests - 100% Passing)**
Valida el CRUD de vehículos, filtros (placa, marca, modelo, año), conteos, asignación de propietarios por admin, generación de lotes de 10 autos y gestión del catálogo:
```bash
cd backend/car-service
# En Windows:
.\mvnw.cmd test
# En Linux/Mac:
./mvnw test
```

### **C. Pruebas del Frontend (Angular)**
Valida los servicios `AuthService`, `AutoService` y `CatalogoService` con `HttpTestingController`:
```bash
cd frontend/manage-cars-front
npm test -- --watch=false --browsers=ChromeHeadless
```

---

## Estructura del Repositorio

```text
manage-cars/
├── backend/
│   ├── auth-service/           # Microservicio de Autenticación y Usuarios (Hexagonal)
│   └── car-service/            # Microservicio de Vehículos y Catálogo (Hexagonal)
├── frontend/
│   └── manage-cars-front/      # SPA en Angular 19 (Glassmorphism Dark UI)
├── docker-compose.yml          # Orquestación de contenedores
├── database_script.sql         # Script SQL idempotente de inicialización
├── ManageCars_Postman_Collection.json # Colección de Postman v2.2.0
└── README.md                   # Documentación oficial del proyecto
```
