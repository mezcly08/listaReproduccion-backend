package com.music.playlist.controller;

import com.music.playlist.dto.error.RespuestaError;
import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
import com.music.playlist.service.ListaReproduccionServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.headers.Header;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
@Tag(name = "Listas de Reproducción", description = "Endpoints para la administración de listas de reproducción de música")
@SecurityRequirement(name = "basicAuth")
public class ListaReproduccionControlador {

    private final ListaReproduccionServicio listaReproduccionServicio;

    @PostMapping
    @Operation(
            summary = "Añadir una nueva lista de reproducción",
            description = "Crea una lista de reproducción. Requiere rol ADMIN. Devuelve la URI en Location y la entidad creada."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "Lista de reproducción creada exitosamente",
                    headers = @Header(name = "Location", description = "URI del recurso creado", schema = @Schema(type = "string")),
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaListaReproduccion.class))
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "Datos inválidos en la petición (ej. nombre null, vacío o solo espacios)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Prohibido (requiere rol ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            ),
            @ApiResponse(
                    responseCode = "409",
                    description = "Conflicto: ya existe una lista de reproducción con el mismo nombre",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            )
    })
    public ResponseEntity<RespuestaListaReproduccion> crearListaReproduccion(@Valid @RequestBody SolicitudListaReproduccion solicitud) {
        RespuestaListaReproduccion creada = listaReproduccionServicio.crearListaReproduccion(solicitud);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{listName}")
                .buildAndExpand(creada.getNombre())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @GetMapping
    @Operation(
            summary = "Ver todas las listas de reproducción existentes",
            description = "Devuelve todas las listas de reproducción. Accesible por usuarios con rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Listas obtenidas exitosamente",
                    content = @Content(mediaType = "application/json", array = @ArraySchema(schema = @Schema(implementation = RespuestaListaReproduccion.class)))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            )
    })
    public ResponseEntity<List<RespuestaListaReproduccion>> obtenerTodasListasReproduccion() {
        List<RespuestaListaReproduccion> listas = listaReproduccionServicio.obtenerTodasListasReproduccion();
        return ResponseEntity.ok(listas);
    }

    @GetMapping("/{listName}")
    @Operation(
            summary = "Ver la descripción y contenido de una lista seleccionada",
            description = "Obtiene una lista de reproducción según su nombre. Accesible por rol USER o ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "Lista encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaListaReproduccion.class))
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lista de reproducción no encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            )
    })
    public ResponseEntity<RespuestaListaReproduccion> obtenerListaReproduccionPorNombre(
            @Parameter(description = "Nombre de la lista de reproducción", example = "Lista 1")
            @PathVariable("listName") String nombreLista) {
        RespuestaListaReproduccion lista = listaReproduccionServicio.obtenerListaReproduccionPorNombre(nombreLista);
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{listName}")
    @Operation(
            summary = "Eliminar una lista de reproducción",
            description = "Elimina la lista de reproducción identificada por su nombre. Requiere rol ADMIN."
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "204",
                    description = "Lista eliminada exitosamente (No Content)"
            ),
            @ApiResponse(
                    responseCode = "401",
                    description = "No autenticado",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            ),
            @ApiResponse(
                    responseCode = "403",
                    description = "Prohibido (requiere rol ADMIN)",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "Lista de reproducción no encontrada",
                    content = @Content(mediaType = "application/json", schema = @Schema(implementation = RespuestaError.class))
            )
    })
    public ResponseEntity<Void> eliminarListaReproduccionPorNombre(
            @Parameter(description = "Nombre de la lista de reproducción a eliminar", example = "Lista 1")
            @PathVariable("listName") String nombreLista) {
        listaReproduccionServicio.eliminarListaReproduccionPorNombre(nombreLista);
        return ResponseEntity.noContent().build();
    }
}
