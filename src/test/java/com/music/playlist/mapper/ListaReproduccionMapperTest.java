package com.music.playlist.mapper;

import com.music.playlist.dto.response.RespuestaCancion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
import com.music.playlist.entity.Cancion;
import com.music.playlist.entity.ListaReproduccion;
import com.music.playlist.entity.ListaReproduccionCancion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class ListaReproduccionMapperTest {

    private ListaReproduccionMapper mapper;

    @BeforeEach
    void setUp() {
        mapper = new ListaReproduccionMapperImpl();
    }

    @Test
    @DisplayName("Debe mapear ListaReproduccion y sus canciones intermedias a RespuestaListaReproduccion")
    void debeMapearListaReproduccionARespuesta() {
        Cancion cancion = Cancion.builder()
                .id(1L)
                .titulo("Smells Like Teen Spirit")
                .artista("Nirvana")
                .album("Nevermind")
                .anno("1991")
                .genero("Grunge")
                .build();

        ListaReproduccion lista = new ListaReproduccion("Grunge 90s", "Lo mejor de Seattle");
        lista.agregarCancion(cancion);

        RespuestaListaReproduccion respuesta = mapper.aRespuesta(lista);

        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getNombre()).isEqualTo("Grunge 90s");
        assertThat(respuesta.getDescripcion()).isEqualTo("Lo mejor de Seattle");
        assertThat(respuesta.getCanciones()).hasSize(1);

        RespuestaCancion cancionRespuesta = respuesta.getCanciones().get(0);
        assertThat(cancionRespuesta.getTitulo()).isEqualTo("Smells Like Teen Spirit");
        assertThat(cancionRespuesta.getArtista()).isEqualTo("Nirvana");
        assertThat(cancionRespuesta.getAlbum()).isEqualTo("Nevermind");
        assertThat(cancionRespuesta.getAnno()).isEqualTo("1991");
        assertThat(cancionRespuesta.getGenero()).isEqualTo("Grunge");
    }

    @Test
    @DisplayName("Debe mapear lista de entidades ListaReproduccion a lista de respuestas")
    void debeMapearListaDeEntidadesAListaDeRespuestas() {
        ListaReproduccion lista1 = new ListaReproduccion("Lista 1", "Desc 1");
        ListaReproduccion lista2 = new ListaReproduccion("Lista 2", "Desc 2");

        List<RespuestaListaReproduccion> respuestas = mapper.aListaRespuestas(List.of(lista1, lista2));

        assertThat(respuestas).hasSize(2);
        assertThat(respuestas.get(0).getNombre()).isEqualTo("Lista 1");
        assertThat(respuestas.get(1).getNombre()).isEqualTo("Lista 2");
    }

    @Test
    @DisplayName("Debe retornar null cuando se pasan entidades nulas")
    void debeRetornarNullConEntidadesNulas() {
        assertThat(mapper.aRespuesta(null)).isNull();
        assertThat(mapper.aListaRespuestas(null)).isNull();
    }

    @Test
    @DisplayName("cancionListaARespuestaCancion debe retornar null si la relación o la canción es nula")
    void cancionListaARespuestaCancion_debeRetornarNullSiCancionEsNula() {
        assertThat(mapper.cancionListaARespuestaCancion(null)).isNull();
        assertThat(mapper.cancionListaARespuestaCancion(new ListaReproduccionCancion())).isNull();
    }
}
