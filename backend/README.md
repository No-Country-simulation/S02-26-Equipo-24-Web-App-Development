# Justina Backend - Plataforma de Simulación Quirúrgica

<p align="center">
  <a href="https://spring.io/projects/spring-boot">
    <img src="https://img.shields.io/badge/Spring%20Boot-4.0.2-brightgreen.svg" alt="Spring Boot 4.0.2">
  </a>
  <a href="https://openjdk.org/projects/jdk/21/">
    <img src="https://img.shields.io/badge/Java-21-blue.svg" alt="Java 21">
  </a>
  <a href="https://maven.apache.org/">
    <img src="https://img.shields.io/badge/Maven-3.9+-blue.svg" alt="Maven">
  </a>
  <a href="https://www.docker.com/">
    <img src="https://img.shields.io/badge/Docker-Ready-blue.svg" alt="Docker">
  </a>
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
  </a>
</p>

> **Autor:** Anthony Parra  
> **Descripción:** Backend para plataforma de simulación quirúrgica con análisis de IA en tiempo real

---

## 📋 Tabla de Contenidos

- [Descripción General](#descripción-general)
- [Arquitectura](#arquitectura)
- [Tecnologías](#tecnologías)
- [Estructura del Proyecto](#estructura-del-proyecto)
- [Primeros Pasos](#primeros-pasos)
  - [Requisitos Previos](#requisitos-previos)
  - [Instalación](#instalación)
  - [Configuración](#configuración)
- [Documentación de la API](#documentación-de-la-api)
- [Endpoints WebSocket](#endpoints-websocket)
- [Seguridad](#seguridad)
- [Pruebas](#pruebas)
- [Despliegue](#despliegue)
- [Variables de Entorno](#variables-de-entorno)
- [Credenciales por Defecto](#credenciales-por-defecto)

---

## Descripción General

Justina Backend es una **plataforma de simulación quirúrgica** construida con Spring Boot que proporciona recolección de datos de telemetría en tiempo real, seguimiento de trayectorias y análisis con IA para procedimientos quirúrgicos. El sistema permite a los cirujanos realizar cirugías simuladas mientras captura datos de movimiento, que luego pueden ser analizados por un sistema de IA para proporcionar retroalimentación y puntuación.

### Características Principales

- 🔐 **Autenticación JWT** - Token seguro con control de acceso basado en roles
- 📡 **Comunicación WebSocket en Tiempo Real** - Streaming de telemetría desde simulaciones quirúrgicas
- 🤖 **Integración con IA** - Análisis avanzado mediante un pipeline de 5 pasos para puntear destreza y riesgo
- 🗂️ **Seguimiento de Trayectorias** - Registro completo de movimientos durante los procedimientos
- 📊 **API RESTful** - Operaciones completas con documentación OpenAPI/Swagger
- 🐳 **Soporte Docker** - Listo para despliegue en contenedores

---

## Arquitectura

Justina Backend sigue principios de **Arquitectura Limpia (Clean Architecture)** con un patrón de **Arquitectura Hexagonal** (Ports & Adapters):

```
┌─────────────────────────────────────────────────────────────────────┐
│                     CAPA DE PRESENTACIÓN                            │
│  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────────────┐ │
│  │ Controladores   │  │ Handlers        │  │ Filtros de          │ │
│  │    REST         │  │  WebSocket      │  │ Seguridad           │ │
│  └────────┬────────┘  └────────┬────────┘  └──────────┬──────────┘ │
└───────────┼─────────────────────┼─────────────────────┼─────────────┘
            │                     │                     │
            ▼                     ▼                     ▼
┌─────────────────────────────────────────────────────────────────────┐
│                      CAPA DE APLICACIÓN                            │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                    Servicios de Aplicación                    │  │
│  │  ┌─────────────┐  ┌─────────────┐  ┌─────────────────────┐   │  │
│  │  │AuthService  │  │SurgeryService│ │ TelemetryService   │   │  │
│  │  └─────────────┘  └─────────────┘  └─────────────────────┘   │  │
│  └──────────────────────────────────────────────────────────────┘  │
│                           PUERTOS (Interfaces)                      │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                        CAPA DE DOMINIO                             │
│  ┌────────────────┐  ┌────────────────┐  ┌──────────────────────┐  │
│  │    Modelos     │  │      DTOs      │  │     Excepciones      │  │
│  │  - User        │  │- AuthResponse  │  │- AuthException       │  │
│  │  - SurgerySession│ │- TelemetryDTO  │  │- SurgeryNotFound    │  │
│  │  - Movement     │  │- TrajectoryDTO │  │- ForbiddenAction    │  │
│  └────────────────┘  └────────────────┘  └──────────────────────┘  │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │               Repositorios de Dominio                         │  │
│  └──────────────────────────────────────────────────────────────┘  │
└──────────────────────────────┬──────────────────────────────────────┘
                               │
                               ▼
┌─────────────────────────────────────────────────────────────────────┐
│                    CAPA DE INFRAESTRUCTURA                         │
│  ┌──────────────────────────────────────────────────────────────┐  │
│  │                        ADAPTADORES                           │  │
│  │  ┌─────────────────┐  ┌─────────────────┐  ┌─────────────┐ │  │
│  │  │ Persistencia    │  │ Seguridad       │  │ WebSocket   │ │  │
│  │  │ (JPA/Hibernate) │  │ (JWT/BCrypt)    │  │ (STOMP/WS)  │ │  │
│  │  └─────────────────┘  └─────────────────┘  └─────────────┘ │  │
│  └──────────────────────────────────────────────────────────────┘  │
└─────────────────────────────────────────────────────────────────────┘
```

### Patrones de Diseño Utilizados

| Patrón                        | Implementación |
|-------------------------------|----------------|
| **Inyección de Dependencias** | Contenedor IoC de Spring con `@Autowired` e Inyección por Constructor |
| **Patrón Repositorio**        | Capa de abstracción sobre persistencia de datos |
| **Patrón Factory**            | `ObjectMapper` para serialización/deserialización JSON |
| **Patrón Strategy**           | Estrategias de seguridad intercambiables (JWT y WebSocket) |
| **Patrón Observer**           | Sesiones WebSocket para actualizaciones en tiempo real |
| **Patrón DTO**                | Objetos de Transferencia de Datos para comunicación API |

---

## Tecnologías

| Categoría | Tecnología | Versión |
|-----------|------------|---------|
| **Framework** | Spring Boot | 4.0.2 |
| **Lenguaje** | Java | 21 |
| **Herramienta de Build** | Maven | 3.9+ |
| **Base de Datos** | H2 (dev) / PostgreSQL | - / 15+ |
| **ORM** | Spring Data JPA / Hibernate | - |
| **Seguridad** | Spring Security + JWT | - |
| **WebSocket** | Spring WebSocket | - |
| **Documentación API** | SpringDoc OpenAPI | 2.8.6 |
| **Pruebas** | JUnit 5 / Mockito | - |
| **Contenedor** | Docker | Latest |

---

## Estructura del Proyecto

```
src/
├── main/
│   ├── java/
│   │   └── project/
│   │       └── Justina/
│   │           ├── JustinaApplication.java          # Punto de Entrada
│   │           ├── application/
│   │           │   ├── ports/                        # Interfaces de Puertos
│   │           │   └── service/                     # Servicios de Aplicación
│   │           │       ├── AuthService.java
│   │           │       └── SurgeryService.java
│   │           ├── domain/
│   │           │   ├── model/                       # Entidades de Dominio
│   │           │   │   ├── User.java
│   │           │   │   ├── SurgerySession.java
│   │           │   │   ├── Movement.java
│   │           │   │   └── SurgeryEvent.java
│   │           │   ├── dto/                         # Objetos de Transferencia
│   │           │   │   ├── LoginRequestDTO.java
│   │           │   │   ├── AuthResponseDTO.java
│   │           │   │   ├── TelemetryDTO.java
│   │           │   │   ├── TrajectoryDTO.java
│   │           │   │   └── AnalysisDTO.java
│   │           │   ├── repository/                  # Interfaces de Repositorio
│   │           │   │   ├── UserRepository.java
│   │           │   │   └── SurgeryRepository.java
│   │           │   └── exception/                  # Excepciones de Dominio
│   │           │       ├── AuthException.java
│   │           │       ├── SurgeryNotFoundException.java
│   │           │       ├── ForbiddenActionException.java
│   │           │       └── UserAlreadyExistsException.java
│   │           └── infrastructure/
│   │               ├── adapter/                     # Adaptadores de Infraestructura
│   │               │   ├── entity/                 # Entidades JPA
│   │               │   ├── mapper/                 # Mapeadores Entidad-DTO
│   │               │   ├── repository/             # Repositorios JPA
│   │               │   ├── UserPersistenceAdapter.java
│   │               │   └── SurgeryPersistenceAdapter.java
│   │               ├── config/                     # Clases de Configuración
│   │               │   ├── DataInitializer.java
│   │               │   └── OpenApiConfig.java
│   │               ├── controller/                 # Controladores REST
│   │               │   ├── AuthController.java
│   │               │   ├── SurgeryController.java
│   │               │   └── GlobalExceptionHandler.java
│   │               ├── security/                   # Infraestructura de Seguridad
│   │               │   ├── SecurityConfig.java
│   │               │   ├── JwtService.java
│   │               │   ├── JwtAuthenticationFilter.java
│   │               │   ├── JwtAuthenticationEntryPoint.java
│   │               │   └── ApplicationConfig.java
│   │               └── websocket/                  # Infraestructura WebSocket
│   │                   ├── WebSocketConfig.java
│   │                   ├── SimulationWebSocketHandler.java
│   │                   ├── AIWebSocketHandler.java
│   │                   └── HandshakeInterceptorImpl.java
│   └── resources/
│       └── application.properties                 # Configuración de la App
└── test/
    └── java/
        └── project/
            └── Justina/
                ├── application/service/           # Pruebas de Servicios
                ├── domain/                         # Pruebas de Dominio
                ├── infrastructure/
                │   ├── controller/                 # Pruebas de Controladores
                │   └── websocket/                  # Pruebas de WebSocket
                └── JustinaApplicationTests.java   # Pruebas de Integración
```

---

## Primeros Pasos

### Requisitos Previos

Asegúrate de tener instalado:

- ☕ **Java Development Kit (JDK)** 21 o superior
- 📦 **Maven** 3.9 o superior
- 🐳 **Docker** y **Docker Compose** (opcional, para despliegue en contenedores)
- 💾 **PostgreSQL** 15+ (opcional, para producción)

### Instalación

1. **Clonar el repositorio**

```bash
git clone <repository-url>
cd S02-26-Equipo-24-Web-App-Development/backend
```

2. **Compilar el proyecto**

```bash
./mvnw clean install -DskipTests
```

3. **Ejecutar la aplicación**

```bash
./mvnw spring-boot:run
```

O usar el JAR ejecutable:

```bash
java -jar target/Justina-0.0.1-SNAPSHOT.jar
```

### Configuración

La aplicación usa variables de entorno para la configuración. Crea un archivo `.env` o configura estas variables en tu sistema:

#### Variables de Entorno Requeridas

| Variable | Descripción | Valor por Defecto |
|----------|-------------|-------------------|
| `PORT` | Puerto del servidor | `8080` |
| `JWT_SECRET_KEY` | Clave secreta para firmar JWT | (debe configurarse) |
| `SPRING_DATASOURCE_URL` | URL de conexión a la base de datos | `jdbc:h2:mem:justina` |
| `SPRING_DATASOURCE_USERNAME` | Usuario de base de datos | `sa` |
| `SPRING_DATASOURCE_PASSWORD` | Contraseña de base de datos | (vacío) |
| `SPRING_DATASOURCE_DRIVER_CLASS_NAME` | Driver JDBC | `org.h2.Driver` |
| `SPRING_JPA_HIBERNATE_DDL_AUTO` | Modo de esquema Hibernate | `create-drop` |
| `SPRING_JPA_SHOW_SQL` | Mostrar consultas SQL | `false` |
| `SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT` | Dialecto Hibernate | `org.hibernate.dialect.H2Dialect` |

#### Configuración de PostgreSQL (Producción)

```properties
# Configura esto para PostgreSQL en producción:
SPRING_DATASOURCE_URL=jdbc:postgresql://localhost:5432/justina
SPRING_DATASOURCE_USERNAME=tu_usuario
SPRING_DATASOURCE_PASSWORD=tu_contraseña
SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
SPRING_JPA_HIBERNATE_DDL_AUTO=update
SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
```

---

## Documentación de la API

Una vez que la aplicación esté ejecutándose, accede a la documentación interactiva de la API:

| Herramienta | URL |
|-------------|-----|
| **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` |
| **OpenAPI JSON** | `http://localhost:8080/v3/api-docs` |
| **OpenAPI YAML** | `http://localhost:8080/v3/api-docs.yaml` |

### Endpoints REST

#### Autenticación

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `POST` | `/api/v1/auth/login` | Inicio de sesión | Público |
| `POST` | `/api/v1/auth/register` | Registro de usuario | Público |
| `GET` | `/api/v1/auth/me` | Obtener usuario actual | Autenticado |

#### Gestión de Cirugías

| Método | Endpoint | Descripción | Roles |
|--------|----------|-------------|-------|
| `GET` | `/api/v1/surgeries/{id}/trajectory` | Obtener trayectoria | CIRUJANO, IA |
| `POST` | `/api/v1/surgeries/{id}/analysis` | Guardar análisis de IA | IA |

---

## Endpoints WebSocket

### URL de Conexión

```
ws://localhost:8080/ws/{endpoint}
```

### Endpoints

| Endpoint | Descripción | Autenticación |
|----------|-------------|----------------|
| `/ws/simulation` | Telemetría quirúrgica en tiempo real | JWT vía query param |
| `/ws/ai` | Canal de notificaciones de IA | JWT con rol ROLE_AI |

### Formatos de Mensajes WebSocket

#### Telemetría de Entrada (Cliente → Servidor)

```json
{
  "coordinates": {"x": 10.5, "y": 20.3, "z": 15.7},
  "event": "MOVE",
  "timestamp": "2024-01-15T10:30:00"
}
```

#### Completado de Cirugía (Servidor → Cliente)

```json
{
  "status": "SAVED",
  "surgeryId": "550e8400-e29b-41d4-a716-446655440000"
}
```

#### Notificación a IA (Servidor → IA)

```json
{
  "event": "NEW_SURGERY",
  "surgeryId": "550e8400-e29b-41d4-a716-446655440000"
}
```

| Evento | Descripción |
|--------|-------------|
| `START` | Inicio de la sesión quirúrgica |
| `MOVE` | Movimiento registrado durante la cirugía |
| `TUMOR_TOUCH` | Contacto accidental con el tumor |
| `KIDNEY_TOUCH` | Contacto accidental con tejido sano (riñón) |
| `HEMORRHAGE` | Hemorragia detectada |
| `TUMOR_REMOVAL` | Fragmento de tumor removido con éxito |
| `FINISH` | Fin de la sesión quirúrgica |
| `NONE` | Estado sin evento |

---

## Seguridad

### Flujo de Autenticación

1. **Login**: El usuario envía credenciales a `/api/v1/auth/login`
2. **Generación de Token**: El servidor valida y retorna el token JWT
3. **Uso del Token**: Incluir token en el header `Authorization`: `Bearer <token>`
4. **Opción Cookie**: Token también configurado como cookie HttpOnly para clientes web

### Control de Acceso Basado en Roles (RBAC)

| Rol | Descripción | Permisos |
|-----|-------------|----------|
| `ROLE_CIRUJANO` / `ROLE_SURGEON` | Usuario cirujano | Login, ver cirugías propias, iniciar simulaciones |
| `ROLE_IA` | Sistema de IA | Recibir notificaciones de cirugías, enviar análisis |

### Configuración de Seguridad

- ✅ Autenticación sin estado basada en JWT
- ✅ Hash de contraseñas con BCrypt
- ✅ CORS habilitado para orígenes del frontend
- ✅ Protección de endpoints basada en roles
- ✅ Intercepción de conexiones WebSocket
- ✅ Cookies seguras HttpOnly

---

## Pruebas

Ejecutar el conjunto completo de pruebas:

```bash
./mvnw test
```

Ejecutar clases de prueba específicas:

```bash
# Pruebas unitarias de AuthService
./mvnw test -Dtest=AuthServiceTest

# Pruebas de integración de Controladores
./mvnw test -Dtest=AuthControllerTest
```

### Áreas de Cobertura de Pruebas

- ✅ Autenticación (login, registro, validación de tokens)
- ✅ Autorización (acceso basado en roles)
- ✅ Obtención de trayectorias quirúrgicas
- ✅ Envío de análisis de IA
- ✅ Conexiones WebSocket
- ✅ Manejo de excepciones
- ✅ Comportamiento de modelos de dominio

---

## Despliegue

### Despliegue con Docker

1. **Construir la imagen Docker**

```bash
docker build -t justina-backend:latest .
```

2. **Ejecutar el contenedor**

```bash
docker run -p 8080:8080 \
  -e JWT_SECRET_KEY=tu-clave-secreta \
  -e SPRING_DATASOURCE_URL=jdbc:postgresql://host:5432/justina \
  -e SPRING_DATASOURCE_USERNAME=postgres \
  -e SPRING_DATASOURCE_PASSWORD=password \
  justina-backend:latest
```

### Docker Compose (Recomendado)

```yaml
version: '3.8'

services:
  justina-backend:
    build: .
    ports:
      - "8080:8080"
    environment:
      - PORT=8080
      - JWT_SECRET_KEY=tu-clave-secreta-aqui
      - SPRING_DATASOURCE_URL=jdbc:postgresql://postgres:5432/justina
      - SPRING_DATASOURCE_USERNAME=postgres
      - SPRING_DATASOURCE_PASSWORD=postgres
      - SPRING_DATASOURCE_DRIVER_CLASS_NAME=org.postgresql.Driver
      - SPRING_JPA_HIBERNATE_DDL_AUTO=update
      - SPRING_JPA_PROPERTIES_HIBERNATE_DIALECT=org.hibernate.dialect.PostgreSQLDialect
    depends_on:
      - postgres

  postgres:
    image: postgres:15
    environment:
      - POSTGRES_DB=justina
      - POSTGRES_USER=postgres
      - POSTGRES_PASSWORD=postgres
    volumes:
      - postgres-data:/var/lib/postgresql/data

volumes:
  postgres-data:
```

Ejecutar con Docker Compose:

```bash
docker-compose up -d
```

---

## Credenciales por Defecto

La aplicación inicializa con usuarios por defecto al primer inicio:

| Usuario | Contraseña | Rol |
|---------|------------|-----|
| `surgeon_master` | `justina2024` | ROLE_SURGEON |
| `ia_justina` | `ia_secret_2024` | ROLE_IA |

> ⚠️ **Importante:** ¡Cambia estas contraseñas en producción!

---

## Contribuir

1. Haz un fork del repositorio
2. Crea tu rama de característica (`git checkout -b feature/caracteristica-increible`)
3. Commitea tus cambios (`git commit -m 'Agregar alguna caracteristica increible'`)
4. Push a la rama (`git push origin feature/caracteristica-increible`)
5. Abre un Pull Request

---

## Licencia

Este proyecto está licenciado bajo la Licencia MIT - consulta el archivo [LICENSE](LICENSE) para más detalles.

---

## Agradecimientos

- [Spring Boot](https://spring.io/projects/spring-boot)
- [Spring Security](https://spring.io/projects/spring-security)
- [OpenAPI Initiative](https://www.openapis.org/)
- [JWT](https://jwt.io/)

---

