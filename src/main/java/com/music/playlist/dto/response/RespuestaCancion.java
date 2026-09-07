package com.music.playlist.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos de una canción perteneciente a una lista de reproducción")
public class RespuestaCancion {

    @Schema(description = "Título de la canción", example = "Bohemian Rhapsody")
    private String titulo;

    @Schema(description = "Artista o banda intérprete", example = "Queen")
    private String artista;

    @Schema(description = "Álbum musical al que pertenece", example = "A Night at the Opera")
    private String album;

    @Schema(description = "Año de lanzamiento", example = "1975")
    private String anno;

    @Schema(description = "Género musical", example = "Rock")
    private String genero;
}
