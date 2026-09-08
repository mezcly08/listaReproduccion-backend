package com.music.playlist.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "Datos para asociar o registrar una canción")
public class SolicitudCancion {

    @NotBlank(message = "El título de la canción es obligatorio")
    @Size(max = 150, message = "El título de la canción no puede exceder los 150 caracteres")
    @Schema(description = "Título de la canción", example = "Bohemian Rhapsody", requiredMode = Schema.RequiredMode.REQUIRED)
    private String titulo;

    @NotBlank(message = "El artista de la canción es obligatorio")
    @Size(max = 150, message = "El artista de la canción no puede exceder los 150 caracteres")
    @Schema(description = "Artista o banda intérprete", example = "Queen", requiredMode = Schema.RequiredMode.REQUIRED)
    private String artista;

    @Size(max = 150, message = "El álbum no puede exceder los 150 caracteres")
    @Schema(description = "Álbum musical al que pertenece", example = "A Night at the Opera")
    private String album;

    @Size(max = 10, message = "El año no puede exceder los 10 caracteres")
    @Schema(description = "Año de lanzamiento", example = "1975")
    private String anno;

    @Size(max = 100, message = "El género no puede exceder los 100 caracteres")
    @Schema(description = "Género musical", example = "Rock")
    private String genero;
}
