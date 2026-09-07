# API de Administración de Listas de Reproducción (Playlist API)

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![Spring Security](https://img.shields.io/badge/Spring%20Security-6.3-green.svg)](https://spring.io/projects/spring-security)
[![H2 Database](https://img.shields.io/badge/Database-H2%20In--Memory-blue.svg)](https://www.h2database.com/)
[![MapStruct](https://img.shields.io/badge/MapStruct-1.5.5.Final-yellow.svg)](https://mapstruct.org/)
[![Swagger / OpenAPI](https://img.shields.io/badge/Documentation-OpenAPI%203%20%2F%20Swagger-green.svg)](https://swagger.io/)
[![Tests](https://img.shields.io/badge/Tests-66%20Passed-success.svg)](https://junit.org/junit5/)
[![Coverage](https://img.shields.io/badge/Coverage-93.2%25%20Lines%20%7C%2090.6%25%20Branches-brightgreen.svg)](target/site/jacoco/index.html)

API REST empresarial para la gestión y administración de listas de reproducción de música desarrollada bajo el patrón de **Arquitectura en Capas** (`Controller` -> `Service` -> `Repository`), utilizando **Java 21**, **Spring Boot 3.3.3**, **Spring Data JPA**, **Spring Security**, **Lombok** y **MapStruct**.

---

## 1. Arquitectura por Capas

El proyecto sigue una separación rigurosa de responsabilidades:

```
[ Petición HTTP ] (curl, Postman, Frontend)
       │
       ▼
[ Capa Controlador ]     ──> ListaReproduccionControlador (Valida DTOs, códigos HTTP, OpenAPI)
       │
       ▼
[ Capa de Servicio ]     ──> ListaReproduccionServicio / ListaReproduccionServicioImpl (@Transactional)
       │
       ├──> [ Mappers ]        ──> ListaReproduccionMapper, CancionMapper (MapStruct compilado)
       │
       ▼
[ Capa Repositorio ]     ──> ListaReproduccionRepository, CancionRepository, ListaReproduccionCancionRepository
       │
       ▼
[ Base de Datos (H2) ]   ──> Tablas relacionales: listas_reproduccion, canciones, lista_reproduccion_canciones
```

### Organización de Paquetes:

- **`com.music.playlist.controller`**: Controladores REST con documentación OpenAPI/Swagger (`ListaReproduccionControlador`).
- **`com.music.playlist.service`**: Interfaz `ListaReproduccionServicio` e implementación `ListaReproduccionServicioImpl` con `@Transactional`. Orquesta las reglas de negocio, asociación de canciones y persistencia.
- **`com.music.playlist.repository`**: Repositorios Spring Data JPA (`ListaReproduccionRepository`, `CancionRepository`, `ListaReproduccionCancionRepository`).
- **`com.music.playlist.entity`**: Entidades JPA con Lombok (`ListaReproduccion`, `Cancion`, `ListaReproduccionCancion`).
- **`com.music.playlist.mapper`**: Mappers compilados en tiempo de compilación con **MapStruct** (`ListaReproduccionMapper`, `CancionMapper`).
- **`com.music.playlist.dto`**: 
  - `request`: Solicitudes con anotaciones de validación Bean Validation (`SolicitudListaReproduccion`, `SolicitudCancion`).
  - `response`: Respuestas formateadas para clientes (`RespuestaListaReproduccion`, `RespuestaCancion`).
  - `error`: Formato unificado de errores (`RespuestaError`).
- **`com.music.playlist.exception`**: Excepciones de negocio (`ListaReproduccionNoEncontradaExcepcion`, `ListaReproduccionYaExisteExcepcion`) y manejador centralizado `@RestControllerAdvice` (`ManejadorExcepcionesGlobal`).
- **`com.music.playlist.config`**: Configuración de Spring Security (`ConfiguracionSeguridad`), OpenAPI (`ConfiguracionOpenApi`) y cargador de datos iniciales (`ConfiguracionInicializadorDatos`).

---

## 2. Modelo de Datos y Reglas de Negocio

### Relación Many-to-Many con Tabla Intermedia Propia
- La relación entre `ListaReproduccion` y `Cancion` se modela mediante la entidad intermedia **`ListaReproduccionCancion`**, con su propia clave primaria autoincremental `id`.
- Esto permite registrar atributos de auditoría o metadatos de relación en el futuro y evita problemas de rendimiento asociados a colecciones simples `@ManyToMany`.

### Regla Crítica de Integridad Referencial
> [!IMPORTANT]
> **Preservación del Catálogo de Canciones**:
> La lista de reproducción mantiene una relación de cascada `CascadeType.ALL` y `orphanRemoval = true` **únicamente hacia la tabla intermedia** `lista_reproduccion_canciones`.
> Al eliminar una lista de reproducción (`DELETE /lists/{listName}`), **solo se eliminan los registros de vinculación en la tabla intermedia**. Las canciones físicas registradas en la tabla `canciones` **permanecen intactas** en el catálogo general y continúan disponibles para otras listas.

### Unicidad de Listas
- El nombre de la lista de reproducción es único (`@Column(unique = true)`).
- La API valida la existencia previa antes de persistir y devuelve código HTTP `409 Conflict` si ya existe una lista con el mismo nombre.

---

## 3. Seguridad y Control de Acceso

La API utiliza autenticación **HTTP Basic** con contraseñas encriptadas mediante **BCrypt** y arquitectura de sesiones sin estado (**Stateless**).

### Usuarios Preconfigurados:

| Usuario | Contraseña | Rol | Permisos |
|---|---|---|---|
| `user` | `user123` | `USER` | **Solo Lectura**: `GET /lists`, `GET /lists/{listName}` |
| `admin` | `admin123` | `ADMIN` | **Acceso Total**: `GET`, `POST`, `DELETE` |

### Matriz de Autorización por Endpoint:

| Método | Endpoint | Rol Requerido | Acción |
|---|---|---|---|
| `GET` | `/lists` | `USER` o `ADMIN` | Listar todas las listas de reproducción |
| `GET` | `/lists/{listName}` | `USER` o `ADMIN` | Ver detalle y canciones de una lista |
| `POST` | `/lists` | `ADMIN` | Crear una nueva lista de reproducción |
| `DELETE` | `/lists/{listName}` | `ADMIN` | Eliminar una lista de reproducción |
| `GET` | `/swagger-ui/**`, `/api-docs/**` | **Público** | Documentación interactiva de OpenAPI |
| `GET/POST` | `/h2-console/**` | **Público** | Consola de administración de base de datos H2 |

### Formato Unificado de Respuestas de Error

Cualquier error de autenticación (401), autorización (403), validación (400), recurso no encontrado (404) o conflicto (409) produce una respuesta JSON estandarizada:

```json
{
  "status": 403,
  "message": "Acceso denegado: El usuario no cuenta con el rol requerido (ADMIN) para ejecutar esta operación.",
  "errors": null
}
```

---

## 4. Requisitos y Configuración del Entorno

### Requisitos Previos:
- **Java Development Kit (JDK) 21** o superior.
- **Apache Maven 3.8+** (o el wrapper configurado).

### Nota sobre Múltiples Versiones de JDK (Windows):
Si en tu sistema la versión por defecto es Java 8 o inferior, asegúrate de apuntar `JAVA_HOME` a JDK 21 antes de compilar:

```powershell
# En PowerShell:
$env:JAVA_HOME = "C:\Users\USUARIO\.jdks\ms-21.0.12.1"
$env:PATH = "$env:JAVA_HOME\bin;$env:PATH"

# Verificar versión activa:
java -version
```

---

## 5. Compilación y Ejecución

### Comandos de Construcción (CLI Maven):

```bash
# 1. Compilar y ejecutar la suite completa de 58 pruebas automatizadas
mvn clean test

# 2. Empaquetar la aplicación en un archivo JAR ejecutable
mvn clean package

# 3. Iniciar el servidor Spring Boot en localhost:8080
mvn spring-boot:run
```

### Ejecución en IntelliJ IDEA:
1. Abre la carpeta del proyecto en IntelliJ IDEA.
2. El IDE detectará la configuración en `.idea/` con el SDK de Java 21 y la configuración de ejecución **`AplicacionListaReproduccion`**.
3. Haz clic en el botón **Run** (`Shift + F10`).

---

## 6. Documentación Interactiva y Consola H2

Una vez iniciada la aplicación (`http://localhost:8080`), puedes acceder a:

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON Docs**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
- **Consola H2 Database**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console)
  - **Driver Class**: `org.h2.Driver`
  - **JDBC URL**: `jdbc:h2:mem:playlistdb`
  - **User Name**: `sa`
  - **Password**: *(dejar en blanco)*

---

## 7. Catálogo de Endpoints y Ejemplos `cURL`

### 7.1. Crear una Lista de Reproducción
- **Método**: `POST`
- **URL**: `http://localhost:8080/lists`
- **Autenticación**: `admin:admin123` (`ADMIN`)
- **Cabeceras**: `Content-Type: application/json`

#### Solicitud de Ejemplo:
```bash
curl -X POST http://localhost:8080/lists \
  -u admin:admin123 \
  -H "Content-Type: application/json" \
  -d '{
    "nombre": "Rock Classics",
    "descripcion": "Grandes clásicos del rock de todos los tiempos",
    "canciones": [
      {
        "titulo": "Bohemian Rhapsody",
        "artista": "Queen",
        "album": "A Night at the Opera",
        "anno": "1975",
        "genero": "Rock"
      },
      {
        "titulo": "Hotel California",
        "artista": "Eagles",
        "album": "Hotel California",
        "anno": "1976",
        "genero": "Rock"
      }
    ]
  }'
```

#### Respuesta Exitosa (`201 Created`):
- **Header Location**: `http://localhost:8080/lists/Rock%20Classics`
```json
{
  "nombre": "Rock Classics",
  "descripcion": "Grandes clásicos del rock de todos los tiempos",
  "canciones": [
    {
      "titulo": "Bohemian Rhapsody",
      "artista": "Queen",
      "album": "A Night at the Opera",
      "anno": "1975",
      "genero": "Rock"
    },
    {
      "titulo": "Hotel California",
      "artista": "Eagles",
      "album": "Hotel California",
      "anno": "1976",
      "genero": "Rock"
    }
  ]
}
```

#### Posibles Respuestas de Error:
- **`400 Bad Request`**: Si el nombre está vacío o si los datos de alguna canción son inválidos.
- **`401 Unauthorized`**: Si no se envían credenciales.
- **`403 Forbidden`**: Si se intenta crear con el usuario `user:user123` (requiere `ADMIN`).
- **`409 Conflict`**: Si ya existe una lista con el mismo nombre.

---

### 7.2. Obtener Todas las Listas de Reproducción
- **Método**: `GET`
- **URL**: `http://localhost:8080/lists`
- **Autenticación**: `user:user123` o `admin:admin123`

#### Solicitud de Ejemplo:
```bash
curl -X GET http://localhost:8080/lists \
  -u user:user123
```

#### Respuesta Exitosa (`200 OK`):
```json
[
  {
    "nombre": "Rock Classics",
    "descripcion": "Grandes clásicos del rock de todos los tiempos",
    "canciones": [
      {
        "titulo": "Bohemian Rhapsody",
        "artista": "Queen",
        "album": "A Night at the Opera",
        "anno": "1975",
        "genero": "Rock"
      }
    ]
  }
]
```

---

### 7.3. Obtener una Lista de Reproducción por Nombre
- **Método**: `GET`
- **URL**: `http://localhost:8080/lists/{listName}`
- **Autenticación**: `user:user123` o `admin:admin123`

#### Solicitud de Ejemplo:
```bash
curl -X GET "http://localhost:8080/lists/Rock%20Classics" \
  -u user:user123
```

#### Respuesta Exitosa (`200 OK`):
```json
{
  "nombre": "Rock Classics",
  "descripcion": "Grandes clásicos del rock de todos los tiempos",
  "canciones": [
    {
      "titulo": "Bohemian Rhapsody",
      "artista": "Queen",
      "album": "A Night at the Opera",
      "anno": "1975",
      "genero": "Rock"
    }
  ]
}
```

#### Posible Respuesta de Error (`404 Not Found`):
```json
{
  "status": 404,
  "message": "La lista de reproducción con nombre 'Inexistente' no existe",
  "errors": null
}
```

---

### 7.4. Eliminar una Lista de Reproducción
- **Método**: `DELETE`
- **URL**: `http://localhost:8080/lists/{listName}`
- **Autenticación**: `admin:admin123` (`ADMIN`)

#### Solicitud de Ejemplo:
```bash
curl -X DELETE "http://localhost:8080/lists/Rock%20Classics" \
  -u admin:admin123
```

#### Respuesta Exitosa:
- **Código**: `204 No Content` (cuerpo vacío).

#### Posibles Respuestas de Error:
- **`401 Unauthorized`**: Petición sin credenciales.
- **`403 Forbidden`**: Si se ejecuta con usuario `user:user123`.
- **`404 Not Found`**: Si la lista especificada no existe.

---

## 8. Suite de Pruebas Automatizadas y Cobertura de Código

La aplicación cuenta con una suite integral de **66 pruebas automatizadas** con **93.2% de cobertura de líneas** y **90.6% de cobertura de ramas** (JaCoCo):

| Tipo de Prueba | Clase | Cantidad | Cobertura / Descripción |
|---|---|---|---|
| **Integración E2E** | `ListaReproduccionIntegracionTest` | 2 | Flujo completo de la API REST con base de datos H2 real, verificando creación, reutilización de canciones en múltiples listas, borrado y persistencia de canciones huérfanas. |
| **Controlador (Web Slice)** | `ListaReproduccionControladorTest` | 13 | **100% líneas**. Códigos de estado (201, 200, 400, 404, 409), encabezado `Location` y validación Bean Validation anidada. |
| **Seguridad** | `SeguridadIntegracionTest` | 11 | **100% líneas**. Autorización, credenciales erróneas (401), accesos denegados por rol (403), permisos para `USER` y `ADMIN`, y rutas públicas. |
| **Servicio (Unitarias)** | `ListaReproduccionServicioTest` | 15 | **100% líneas / 100% ramas**. Lógica de negocio pura con Mockito: canciones nuevas, existentes o mixtas, listas vacías o nulas, manejo de duplicados y excepciones. |
| **Repositorio (Data Slice)** | `ListaReproduccionRepositoryTest` | 11 | **100% métodos**. Operaciones de persistencia con `@DataJpaTest` y `TestEntityManager`, búsquedas en `CancionRepository`, `ListaReproduccionCancionRepository`, clave única y regla de borrado en cascada. |
| **Manejador de Excepciones** | `ManejadorExcepcionesGlobalTest` | 7 | **100% líneas**. Manejo de todas las excepciones controladas y no controladas (400, 401, 403, 404, 409, 500). |
| **Mappers (Unitarias)** | `ListaReproduccionMapperTest` | 4 | **97.7% instrucciones**. Conversión de entidades `ListaReproduccion`, tablas intermedias y DTOs con MapStruct. |
| **Mappers (Unitarias)** | `CancionMapperTest` | 3 | **100% líneas**. Conversión bidireccional entre `SolicitudCancion`, `Cancion` y `RespuestaCancion`. |

### Ejecutar Pruebas y Generar Reporte de Cobertura:
```bash
# 1. Ejecutar todas las pruebas y generar reporte JaCoCo
mvn test

# 2. Ver el reporte HTML interactivo de cobertura (abrir en navegador)
# Ruta: target/site/jacoco/index.html
```
