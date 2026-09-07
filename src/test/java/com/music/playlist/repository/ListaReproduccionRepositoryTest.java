package com.music.playlist.repository;

import com.music.playlist.entity.Cancion;
import com.music.playlist.entity.ListaReproduccion;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class ListaReproduccionRepositoryTest {

    @Autowired
    private ListaReproduccionRepository listaReproduccionRepository;

    @Autowired
    private CancionRepository cancionRepository;

    @Autowired
    private ListaReproduccionCancionRepository listaReproduccionCancionRepository;

    @Autowired
    private TestEntityManager entityManager;

    private ListaReproduccion listaReproduccion;
    private Cancion cancion1;
    private Cancion cancion2;

    @BeforeEach
    void setUp() {
        cancion1 = cancionRepository.save(new Cancion("Blinding Lights", "The Weeknd", "After Hours", "2020", "Pop"));
        cancion2 = cancionRepository.save(new Cancion("Levitating", "Dua Lipa", "Future Nostalgia", "2020", "Pop"));

        listaReproduccion = new ListaReproduccion("Pop Hits", "Canciones populares");
        listaReproduccion.agregarCancion(cancion1);
        listaReproduccion.agregarCancion(cancion2);

        listaReproduccion = listaReproduccionRepository.save(listaReproduccion);
        entityManager.flush();
        entityManager.clear();
    }

    @Test
    @DisplayName("Debe persistir playlist y vincular canciones")
    void debePersistirPlaylistYCanciones() {
        Optional<ListaReproduccion> encontrada = listaReproduccionRepository.findByNombre("Pop Hits");

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getId()).isNotNull();
        assertThat(encontrada.get().getNombre()).isEqualTo("Pop Hits");
        assertThat(encontrada.get().getCancionesLista()).hasSize(2);
        assertThat(encontrada.get().getCancionesLista().get(0).getCancion().getTitulo()).isEqualTo("Blinding Lights");
    }

    @Test
    @DisplayName("Debe encontrar una playlist por su nombre único")
    void debeBuscarPorNombre() {
        Optional<ListaReproduccion> encontrada = listaReproduccionRepository.findByNombre("Pop Hits");

        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getNombre()).isEqualTo("Pop Hits");
    }

    @Test
    @DisplayName("Debe retornar Optional.empty() cuando la playlist no existe por nombre")
    void debeRetornarEmptyCuandoNoExistePorNombre() {
        Optional<ListaReproduccion> encontrada = listaReproduccionRepository.findByNombre("Inexistente");
        assertThat(encontrada).isEmpty();
    }

    @Test
    @DisplayName("Debe retornar false si la playlist no existe por su nombre")
    void debeRetornarFalseCuandoNoExistePorNombre() {
        boolean existe = listaReproduccionRepository.existsByNombre("Inexistente");
        assertThat(existe).isFalse();
    }

    @Test
    @DisplayName("Debe persistir una playlist vacía sin canciones")
    void debePersistirPlaylistSinCanciones() {
        ListaReproduccion playlistVacia = new ListaReproduccion("Acoustic Instrumental", "Sin canciones todavía");
        ListaReproduccion guardada = listaReproduccionRepository.save(playlistVacia);
        entityManager.flush();

        Optional<ListaReproduccion> encontrada = listaReproduccionRepository.findByNombre("Acoustic Instrumental");
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getCancionesLista()).isEmpty();
    }

    @Test
    @DisplayName("Debe desvincular una canción usando eliminarCancion")
    void debeDesvincularCancionDeLista() {
        ListaReproduccion lista = listaReproduccionRepository.findByNombre("Pop Hits").orElseThrow();
        lista.eliminarCancion(cancion1);
        listaReproduccionRepository.save(lista);
        entityManager.flush();
        entityManager.clear();

        ListaReproduccion actualizada = listaReproduccionRepository.findByNombre("Pop Hits").orElseThrow();
        assertThat(actualizada.getCancionesLista()).hasSize(1);
    }

    @Test
    @DisplayName("Debe verificar unicidad de nombre de playlist a nivel de base de datos")
    void debeLanzarExcepcionPorNombreDuplicadoEnBaseDeDatos() {
        ListaReproduccion duplicada = new ListaReproduccion("Pop Hits", "Otra descripción");

        org.assertj.core.api.Assertions.assertThatThrownBy(() -> {
            listaReproduccionRepository.save(duplicada);
            entityManager.flush();
        }).isInstanceOf(org.springframework.dao.DataIntegrityViolationException.class);
    }

    @Test
    @DisplayName("CancionRepository: debe buscar y encontrar canción por título y artista")
    void cancionRepository_debeBuscarPorTituloYArtista() {
        Optional<Cancion> encontrada = cancionRepository.findByTituloAndArtista("Blinding Lights", "The Weeknd");
        assertThat(encontrada).isPresent();
        assertThat(encontrada.get().getAlbum()).isEqualTo("After Hours");
    }

    @Test
    @DisplayName("CancionRepository: debe retornar empty cuando título y artista no existen")
    void cancionRepository_debeRetornarEmptyCuandoNoExiste() {
        Optional<Cancion> noEncontrada = cancionRepository.findByTituloAndArtista("Despacito", "Luis Fonsi");
        assertThat(noEncontrada).isEmpty();
    }

    @Test
    @DisplayName("ListaReproduccionCancionRepository: debe encontrar relaciones por listaId")
    void listaReproduccionCancionRepository_debeBuscarPorListaId() {
        ListaReproduccion lista = listaReproduccionRepository.findByNombre("Pop Hits").orElseThrow();
        var relaciones = listaReproduccionCancionRepository.findByListaReproduccionId(lista.getId());

        assertThat(relaciones).hasSize(2);
    }

    @Test
    @DisplayName("CRÍTICO: Al eliminar la playlist se elimina la tabla intermedia pero NO las canciones")
    void debeEliminarPlaylistSinEliminarCanciones() {
        ListaReproduccion paraEliminar = listaReproduccionRepository.findByNombre("Pop Hits").orElseThrow();
        Long listaId = paraEliminar.getId();
        listaReproduccionRepository.delete(paraEliminar);
        entityManager.flush();

        Optional<ListaReproduccion> despuesDeEliminar = listaReproduccionRepository.findByNombre("Pop Hits");
        assertThat(despuesDeEliminar).isEmpty();

        // 2. Las filas intermedias se eliminan
        assertThat(listaReproduccionCancionRepository.findByListaReproduccionId(listaId)).isEmpty();

        // 3. Las canciones físicas siguen existiendo
        assertThat(cancionRepository.count()).isEqualTo(2L);
    }
}
