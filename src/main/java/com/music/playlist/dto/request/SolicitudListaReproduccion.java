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
public class SolicitudListaReproduccion {

    @NotBlank(message = "El nombre de la lista de reproducción es obligatorio y no puede estar en blanco")
    private String nombre;

    private String descripcion;

    @Valid
    @Builder.Default
    private List<SolicitudCancion> canciones = new ArrayList<>();
}
