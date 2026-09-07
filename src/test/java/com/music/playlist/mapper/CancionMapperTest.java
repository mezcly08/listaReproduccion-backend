package com.music.playlist.mapper;

import com.music.playlist.dto.request.SolicitudCancion;
import com.music.playlist.dto.response.RespuestaCancion;
import com.music.playlist.entity.Cancion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CancionMapperTest {

    private CancionMapper cancionMapper;

    @BeforeEach
    void setUp() {
        cancionMapper = new CancionMapperImpl();
    }

    @Test
    @DisplayName("Debe mapear SolicitudCancion a entidad Cancion ignorando el id")
    void debeMapearSolicitudACancion() {
        SolicitudCancion solicitud = SolicitudCancion.builder()
                .titulo("Hotel California")
                .artista("Eagles")
                .album("Hotel California")
                .anno("1976")
                .genero("Rock")
                .build();

        Cancion entidad = cancionMapper.aEntidad(solicitud);

        assertThat(entidad).isNotNull();
        assertThat(entidad.getId()).isNull();
        assertThat(entidad.getTitulo()).isEqualTo("Hotel California");
        assertThat(entidad.getArtista()).isEqualTo("Eagles");
        assertThat(entidad.getAlbum()).isEqualTo("Hotel California");
        assertThat(entidad.getAnno()).isEqualTo("1976");
        assertThat(entidad.getGenero()).isEqualTo("Rock");
    }

    @Test
    @DisplayName("Debe mapear Cancion a RespuestaCancion")
    void debeMapearCancionARespuesta() {
        Cancion cancion = Cancion.builder()
                .id(1L)
                .titulo("Imagine")
                .artista("John Lennon")
                .album("Imagine")
                .anno("1971")
                .genero("Rock")
                .build();

        RespuestaCancion respuesta = cancionMapper.aRespuesta(cancion);

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getTitulo()).isEqualTo("Imagine");
        assertThat(respuesta.getArtista()).isEqualTo("John Lennon");
        assertThat(respuesta.getAlbum()).isEqualTo("Imagine");
        assertThat(respuesta.getAnno()).isEqualTo("1971");
        assertThat(respuesta.getGenero()).isEqualTo("Rock");
    }

    @Test
    @DisplayName("Debe manejar valores nulos retornando null")
    void debeManejarNulos() {
        assertThat(cancionMapper.aEntidad(null)).isNull();
        assertThat(cancionMapper.aRespuesta(null)).isNull();
    }
}
