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
public class RespuestaListaReproduccion {

    private String nombre;

    private String descripcion;

    @Builder.Default
    private List<RespuestaCancion> canciones = new ArrayList<>();
}
