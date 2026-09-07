package com.music.playlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
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
@Schema(description = "Datos para la creación de una lista de reproducción")
public class SolicitudListaReproduccion {

    @NotBlank(message = "El nombre de la lista de reproducción es obligatorio y no puede estar en blanco")
    @Schema(description = "Nombre único de la lista de reproducción", example = "Rock Classics", requiredMode = Schema.RequiredMode.REQUIRED)
    private String nombre;

    @Schema(description = "Descripción temática de la lista", example = "Las mejores canciones de rock clásico")
    private String descripcion;

    @Valid
    @Schema(description = "Lista inicial de canciones asociadas")
    @Builder.Default
    private List<SolicitudCancion> canciones = new ArrayList<>();
}
