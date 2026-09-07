package com.music.playlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Respuesta que representa la lista de reproducción y sus canciones")
public class RespuestaListaReproduccion {

    @Schema(description = "Nombre único de la lista de reproducción", example = "Rock Classics")
    private String nombre;

    @Schema(description = "Descripción temática de la lista", example = "Las mejores canciones de rock clásico")
    private String descripcion;

    @Schema(description = "Canciones vinculadas a la lista de reproducción")
    @Builder.Default
    private List<RespuestaCancion> canciones = new ArrayList<>();
}
