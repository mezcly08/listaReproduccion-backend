package com.music.playlist.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RespuestaCancion {

    private String titulo;

    private String artista;

    private String album;

    private String anno;

    private String genero;
}
