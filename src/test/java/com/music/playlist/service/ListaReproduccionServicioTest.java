package com.music.playlist.service;

import com.music.playlist.dto.request.SolicitudCancion;
import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.dto.response.RespuestaCancion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
import com.music.playlist.entity.Cancion;
import com.music.playlist.entity.ListaReproduccion;
import com.music.playlist.exception.ListaReproduccionNoEncontradaExcepcion;
import com.music.playlist.exception.ListaReproduccionYaExisteExcepcion;
import com.music.playlist.mapper.CancionMapper;
import com.music.playlist.mapper.ListaReproduccionMapper;
import com.music.playlist.repository.CancionRepository;
import com.music.playlist.repository.ListaReproduccionRepository;
import com.music.playlist.service.impl.ListaReproduccionServicioImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ListaReproduccionServicioTest {

    @Mock
    private ListaReproduccionRepository listaReproduccionRepository;

    @Mock
    private CancionRepository cancionRepository;

    @Mock
    private ListaReproduccionMapper listaReproduccionMapper;

    @Mock
    private CancionMapper cancionMapper;

    @InjectMocks
    private ListaReproduccionServicioImpl listaReproduccionServicio;

    private ListaReproduccion listaEjemplo;
    private Cancion cancionEjemplo;
    private SolicitudListaReproduccion solicitudEjemplo;
    private RespuestaListaReproduccion respuestaEjemplo;

    @BeforeEach
    void setUp() {
        cancionEjemplo = Cancion.builder()
                .id(10L)
                .titulo("Bohemian Rhapsody")
                .artista("Queen")
                .album("A Night at the Opera")
                .anno("1975")
                .genero("Rock")
                .build();

        listaEjemplo = ListaReproduccion.builder()
                .id(1L)
                .nombre("Rock Classics")
                .descripcion("Grandes temas")
                .cancionesLista(new ArrayList<>())
                .build();

        listaEjemplo.agregarCancion(cancionEjemplo);

        SolicitudCancion cancionSolicitud = SolicitudCancion.builder()
                .titulo("Bohemian Rhapsody")
                .artista("Queen")
                .album("A Night at the Opera")
                .anno("1975")
                .genero("Rock")
                .build();

        solicitudEjemplo = SolicitudListaReproduccion.builder()
                .nombre("Rock Classics")
                .descripcion("Grandes temas")
                .canciones(List.of(cancionSolicitud))
                .build();

        RespuestaCancion cancionRespuesta = RespuestaCancion.builder()
                .titulo("Bohemian Rhapsody")
                .artista("Queen")
                .album("A Night at the Opera")
                .anno("1975")
                .genero("Rock")
                .build();

        respuestaEjemplo = RespuestaListaReproduccion.builder()
                .nombre("Rock Classics")
                .descripcion("Grandes temas")
                .canciones(List.of(cancionRespuesta))
                .build();
    }

    @Test
    @DisplayName("Debe crear una playlist exitosamente vinculando la canción existente")
    void debeCrearListaReproduccionExitosamente_conCancionExistente() {
        when(listaReproduccionRepository.existsByNombre("Rock Classics")).thenReturn(false);
        when(cancionRepository.findByTituloAndArtista("Bohemian Rhapsody", "Queen")).thenReturn(Optional.of(cancionEjemplo));
        when(listaReproduccionRepository.save(any(ListaReproduccion.class))).thenReturn(listaEjemplo);
        when(listaReproduccionMapper.aRespuesta(listaEjemplo)).thenReturn(respuestaEjemplo);

        RespuestaListaReproduccion resultado = listaReproduccionServicio.crearListaReproduccion(solicitudEjemplo);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Rock Classics");
        assertThat(resultado.getCanciones()).hasSize(1);
        verify(listaReproduccionRepository).save(any(ListaReproduccion.class));
    }

    @Test
    @DisplayName("Debe crear una playlist y guardar nueva canción si no existe previamente")
    void debeCrearListaReproduccionExitosamente_conCancionNueva() {
        when(listaReproduccionRepository.existsByNombre("Rock Classics")).thenReturn(false);
        when(cancionRepository.findByTituloAndArtista("Bohemian Rhapsody", "Queen")).thenReturn(Optional.empty());
        when(cancionMapper.aEntidad(any(SolicitudCancion.class))).thenReturn(cancionEjemplo);
        when(cancionRepository.save(any(Cancion.class))).thenReturn(cancionEjemplo);
        when(listaReproduccionRepository.save(any(ListaReproduccion.class))).thenReturn(listaEjemplo);
        when(listaReproduccionMapper.aRespuesta(listaEjemplo)).thenReturn(respuestaEjemplo);

        RespuestaListaReproduccion resultado = listaReproduccionServicio.crearListaReproduccion(solicitudEjemplo);

        assertThat(resultado).isNotNull();
        verify(cancionRepository).save(any(Cancion.class));
        verify(listaReproduccionRepository).save(any(ListaReproduccion.class));
    }

    @Test
    @DisplayName("Debe crear una playlist exitosamente cuando la lista de canciones es null")
    void debeCrearListaReproduccionExitosamente_conListaDeCancionesNull() {
        SolicitudListaReproduccion solicitudSinCanciones = SolicitudListaReproduccion.builder()
                .nombre("Acoustic Vibes")
                .descripcion("Sesiones acústicas")
                .canciones(null)
                .build();

        ListaReproduccion playlistGuardada = ListaReproduccion.builder()
                .id(2L)
                .nombre("Acoustic Vibes")
                .descripcion("Sesiones acústicas")
                .cancionesLista(new ArrayList<>())
                .build();

        RespuestaListaReproduccion respuestaSinCanciones = RespuestaListaReproduccion.builder()
                .nombre("Acoustic Vibes")
                .descripcion("Sesiones acústicas")
                .canciones(List.of())
                .build();

        when(listaReproduccionRepository.existsByNombre("Acoustic Vibes")).thenReturn(false);
        when(listaReproduccionRepository.save(any(ListaReproduccion.class))).thenReturn(playlistGuardada);
        when(listaReproduccionMapper.aRespuesta(playlistGuardada)).thenReturn(respuestaSinCanciones);

        RespuestaListaReproduccion resultado = listaReproduccionServicio.crearListaReproduccion(solicitudSinCanciones);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Acoustic Vibes");
        assertThat(resultado.getCanciones()).isEmpty();
        verify(cancionRepository, never()).save(any());
        verify(listaReproduccionRepository).save(any(ListaReproduccion.class));
    }

    @Test
    @DisplayName("Debe crear una playlist exitosamente cuando la lista de canciones está vacía")
    void debeCrearListaReproduccionExitosamente_conListaDeCancionesVacia() {
        SolicitudListaReproduccion solicitudVacia = SolicitudListaReproduccion.builder()
                .nombre("Chillout")
                .descripcion("Música relajante")
                .canciones(new ArrayList<>())
                .build();

        ListaReproduccion playlistGuardada = ListaReproduccion.builder()
                .id(3L)
                .nombre("Chillout")
                .descripcion("Música relajante")
                .cancionesLista(new ArrayList<>())
                .build();

        RespuestaListaReproduccion respuesta = RespuestaListaReproduccion.builder()
                .nombre("Chillout")
                .descripcion("Música relajante")
                .canciones(List.of())
                .build();

        when(listaReproduccionRepository.existsByNombre("Chillout")).thenReturn(false);
        when(listaReproduccionRepository.save(any(ListaReproduccion.class))).thenReturn(playlistGuardada);
        when(listaReproduccionMapper.aRespuesta(playlistGuardada)).thenReturn(respuesta);

        RespuestaListaReproduccion resultado = listaReproduccionServicio.crearListaReproduccion(solicitudVacia);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Chillout");
        assertThat(resultado.getCanciones()).isEmpty();
        verify(cancionRepository, never()).save(any());
        verify(listaReproduccionRepository).save(any(ListaReproduccion.class));
    }

    @Test
    @DisplayName("Debe crear playlist asociando una canción existente y guardando otra nueva en la misma operación")
    void debeCrearListaReproduccionExitosamente_conCancionesMixtasExistentesYNuevas() {
        SolicitudCancion cancionExistenteSol = SolicitudCancion.builder()
                .titulo("Bohemian Rhapsody")
                .artista("Queen")
                .build();

        SolicitudCancion cancionNuevaSol = SolicitudCancion.builder()
                .titulo("Don't Stop Me Now")
                .artista("Queen")
                .album("Jazz")
                .anno("1978")
                .genero("Rock")
                .build();

        Cancion cancionNuevaEntidad = Cancion.builder()
                .id(11L)
                .titulo("Don't Stop Me Now")
                .artista("Queen")
                .album("Jazz")
                .anno("1978")
                .genero("Rock")
                .build();

        SolicitudListaReproduccion solicitudMixta = SolicitudListaReproduccion.builder()
                .nombre("Queen Best")
                .descripcion("Lo mejor de Queen")
                .canciones(List.of(cancionExistenteSol, cancionNuevaSol))
                .build();

        when(listaReproduccionRepository.existsByNombre("Queen Best")).thenReturn(false);
        when(cancionRepository.findByTituloAndArtista("Bohemian Rhapsody", "Queen")).thenReturn(Optional.of(cancionEjemplo));
        when(cancionRepository.findByTituloAndArtista("Don't Stop Me Now", "Queen")).thenReturn(Optional.empty());
        when(cancionMapper.aEntidad(cancionNuevaSol)).thenReturn(cancionNuevaEntidad);
        when(cancionRepository.save(cancionNuevaEntidad)).thenReturn(cancionNuevaEntidad);
        when(listaReproduccionRepository.save(any(ListaReproduccion.class))).thenReturn(listaEjemplo);
        when(listaReproduccionMapper.aRespuesta(listaEjemplo)).thenReturn(respuestaEjemplo);

        RespuestaListaReproduccion resultado = listaReproduccionServicio.crearListaReproduccion(solicitudMixta);

        assertThat(resultado).isNotNull();
        verify(cancionRepository).save(cancionNuevaEntidad);
        verify(cancionRepository, never()).save(cancionEjemplo);
        verify(listaReproduccionRepository).save(any(ListaReproduccion.class));
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre es null")
    void debeLanzarExcepcionCuandoNombreEsNull() {
        SolicitudListaReproduccion solicitudInvalida = SolicitudListaReproduccion.builder()
                .nombre(null)
                .descripcion("Desc")
                .build();

        assertThatThrownBy(() -> listaReproduccionServicio.crearListaReproduccion(solicitudInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser null");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre es cadena vacía")
    void debeLanzarExcepcionCuandoNombreEsCadenaVacia() {
        SolicitudListaReproduccion solicitudInvalida = SolicitudListaReproduccion.builder()
                .nombre("")
                .descripcion("Desc")
                .build();

        assertThatThrownBy(() -> listaReproduccionServicio.crearListaReproduccion(solicitudInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser null");
    }

    @Test
    @DisplayName("Debe lanzar IllegalArgumentException cuando el nombre es solo espacios en blanco")
    void debeLanzarExcepcionCuandoNombreEsSoloEspacios() {
        SolicitudListaReproduccion solicitudInvalida = SolicitudListaReproduccion.builder()
                .nombre("   ")
                .descripcion("Desc")
                .build();

        assertThatThrownBy(() -> listaReproduccionServicio.crearListaReproduccion(solicitudInvalida))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("no puede ser null");
    }

    @Test
    @DisplayName("Debe lanzar ListaReproduccionYaExisteExcepcion cuando la playlist ya existe")
    void debeLanzarExcepcionCuandoListaYaExiste() {
        when(listaReproduccionRepository.existsByNombre("Rock Classics")).thenReturn(true);

        assertThatThrownBy(() -> listaReproduccionServicio.crearListaReproduccion(solicitudEjemplo))
                .isInstanceOf(ListaReproduccionYaExisteExcepcion.class);

        verify(listaReproduccionRepository, never()).save(any());
    }

    @Test
    @DisplayName("Debe retornar lista vacía cuando no existen playlists registradas")
    void debeRetornarListaVaciaCuandoNoHayPlaylists() {
        when(listaReproduccionRepository.findAll()).thenReturn(List.of());
        when(listaReproduccionMapper.aListaRespuestas(List.of())).thenReturn(List.of());

        List<RespuestaListaReproduccion> resultados = listaReproduccionServicio.obtenerTodasListasReproduccion();

        assertThat(resultados).isEmpty();
        verify(listaReproduccionRepository).findAll();
    }

    @Test
    @DisplayName("Debe obtener todas las playlists")
    void debeObtenerTodasListasReproduccionExitosamente() {
        when(listaReproduccionRepository.findAll()).thenReturn(List.of(listaEjemplo));
        when(listaReproduccionMapper.aListaRespuestas(List.of(listaEjemplo))).thenReturn(List.of(respuestaEjemplo));

        List<RespuestaListaReproduccion> resultados = listaReproduccionServicio.obtenerTodasListasReproduccion();

        assertThat(resultados).hasSize(1);
        assertThat(resultados.get(0).getNombre()).isEqualTo("Rock Classics");
        verify(listaReproduccionRepository).findAll();
    }

    @Test
    @DisplayName("Debe obtener una playlist existente por su nombre")
    void debeObtenerListaReproduccionPorNombreExitosamente() {
        when(listaReproduccionRepository.findByNombre("Rock Classics")).thenReturn(Optional.of(listaEjemplo));
        when(listaReproduccionMapper.aRespuesta(listaEjemplo)).thenReturn(respuestaEjemplo);

        RespuestaListaReproduccion resultado = listaReproduccionServicio.obtenerListaReproduccionPorNombre("Rock Classics");

        assertThat(resultado).isNotNull();
        assertThat(resultado.getNombre()).isEqualTo("Rock Classics");
    }

    @Test
    @DisplayName("Debe lanzar ListaReproduccionNoEncontradaExcepcion al consultar playlist inexistente")
    void debeLanzarExcepcionCuandoNoSeEncuentraListaPorNombre() {
        when(listaReproduccionRepository.findByNombre("NonExistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listaReproduccionServicio.obtenerListaReproduccionPorNombre("NonExistent"))
                .isInstanceOf(ListaReproduccionNoEncontradaExcepcion.class);
    }

    @Test
    @DisplayName("Debe eliminar una playlist existente")
    void debeEliminarListaReproduccionExitosamente() {
        when(listaReproduccionRepository.findByNombre("Rock Classics")).thenReturn(Optional.of(listaEjemplo));

        listaReproduccionServicio.eliminarListaReproduccionPorNombre("Rock Classics");

        verify(listaReproduccionRepository).delete(listaEjemplo);
    }

    @Test
    @DisplayName("Debe lanzar ListaReproduccionNoEncontradaExcepcion al intentar eliminar inexistente")
    void debeLanzarExcepcionAlEliminarInexistente() {
        when(listaReproduccionRepository.findByNombre("NonExistent")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> listaReproduccionServicio.eliminarListaReproduccionPorNombre("NonExistent"))
                .isInstanceOf(ListaReproduccionNoEncontradaExcepcion.class);

        verify(listaReproduccionRepository, never()).delete(any());
    }
}
