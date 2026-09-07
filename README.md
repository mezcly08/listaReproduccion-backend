# Playlist Management API (Arquitectura por Capas)

API REST empresarial para la administración de listas de reproducción de música desarrollada bajo el patrón de **Arquitectura en Capas (Controller -> Service -> Repository)**, **Java 21**, **Spring Boot 3.3.3**, **Lombok**, **MapStruct**, **Spring Data JPA** y **Spring Security**.

---

## 1. Arquitectura por Capas

El proyecto sigue una estructura limpia y desacoplada por responsabilidades:

```
[ HTTP Requests ]
       │
       ▼
[ Controller Layer ]  ──> Valida DTOs, documenta con OpenAPI/Swagger, maneja códigos HTTP
       │
       ▼
[ Service Layer ]     ──> Orquesta la lógica de negocio y transacciones (PlaylistServiceImpl)
       │
       ├──> [ Mappers ]        ──> Conversión entre DTOs y Entidades vía MapStruct
       │
       ▼
[ Repository Layer ]  ──> Spring Data JPA para acceso a datos (PlaylistRepository, SongRepository, PlaylistSongRepository)
       │
       ▼
[ Database (H2) ]     ──> Tablas relacionales con integridad referencial (playlists, songs, playlist_songs)
```

### Organización de Paquetes:
- **`com.music.playlist.controller`**: Controladores REST con endpoints documentados con Swagger/OpenAPI.
- **`com.music.playlist.service`**: Interfaz `PlaylistService` e implementación `PlaylistServiceImpl` con `@Transactional`. Toda la lógica de negocio y vinculación reside en esta capa.
- **`com.music.playlist.repository`**: Repositorios Spring Data JPA (`PlaylistRepository`, `SongRepository`, `PlaylistSongRepository`).
- **`com.music.playlist.entity`**: Entidades JPA (`Playlist`, `Song`, `PlaylistSong`) decoradas con Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, `@Builder`).
- **`com.music.playlist.mapper`**: Mappers compilados con **MapStruct** (`PlaylistMapper`, `SongMapper`).
- **`com.music.playlist.dto`**: DTOs para solicitudes (`PlaylistRequest`, `SongRequest`), respuestas (`PlaylistResponse`, `SongResponse`) y errores (`ErrorResponse`).
- **`com.music.playlist.exception`**: Excepciones de negocio y `@RestControllerAdvice` (`GlobalExceptionHandler`).
- **`com.music.playlist.config`**: Configuración de Spring Security, OpenAPI y cargador inicial de datos (`DataInitializerConfig`).

---

## 2. Modelo de Datos y Relación Many-to-Many

- **Tabla Intermedia Escalable (`PlaylistSong`)**:
  - Implementada como una entidad JPA propia con su propia clave primaria `Long id` para máxima escalabilidad y auditoría futura.
- **Integridad y Conservación de Canciones**:
  - La relación de `Playlist` hacia `PlaylistSong` está configurada con `cascade = CascadeType.ALL` y `orphanRemoval = true`.
  - Al eliminar una lista de reproducción (`DELETE /lists/{listName}`), **únicamente se eliminan los registros de la tabla intermedia `playlist_songs`**, garantizando que las canciones físicas (`songs`) permanezcan intactas en el catálogo musical.

---

## 3. Seguridad y Roles

- Autenticación: **HTTP Basic** con contraseñas encriptadas en **BCrypt**.
- Usuarios en memoria:
  - `user` / `user123` (Rol `USER`): Lectura (`GET /lists`, `GET /lists/{listName}`).
  - `admin` / `admin123` (Rol `ADMIN`): Lectura y Escritura completa (`GET`, `POST`, `DELETE`).

---

## 4. Cómo Ejecutar el Proyecto

### Requisitos
- **JDK 21**
- **Maven 3.6+**

### Comandos de Construcción y Ejecución (CLI)
```bash
# Ejecutar los 29 tests automatizados
mvn clean test

# Empaquetar el JAR ejecutable
mvn clean package

# Iniciar la aplicación
mvn spring-boot:run
```

### Ejecutar en IntelliJ IDEA
1. Abre el proyecto seleccionando la carpeta raíz.
2. IntelliJ detectará automáticamente la configuración en `.idea/` con JDK 21, procesamiento de anotaciones (Lombok + MapStruct) y el perfil de ejecución **`PlaylistApplication`**.
3. Haz clic en **Run** (`Shift + F10`).

---

## 5. Documentación Interactiva y Postman

- **Swagger UI**: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- **OpenAPI JSON**: [http://localhost:8080/api-docs](http://localhost:8080/api-docs)
- **Consola H2**: [http://localhost:8080/h2-console](http://localhost:8080/h2-console) (JDBC URL: `jdbc:h2:mem:playlistdb` | Usuario: `sa` | Clave: vacía).
- **Colección Postman**: Archivo listo para importar en `postman/Playlist_API.postman_collection.json`.
