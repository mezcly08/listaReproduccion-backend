package com.music.playlist.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudCancion {

    @NotBlank(message = "El título de la canción es obligatorio")
    private String titulo;

    @NotBlank(message = "El artista de la canción es obligatorio")
    private String artista;

    private String album;

    private String anno;

    private String genero;
}
