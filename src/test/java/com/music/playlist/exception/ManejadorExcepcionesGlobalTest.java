package com.music.playlist.exception;

import com.music.playlist.dto.error.RespuestaError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import static org.assertj.core.api.Assertions.assertThat;

class ManejadorExcepcionesGlobalTest {

    private ManejadorExcepcionesGlobal manejador;

    @BeforeEach
    void setUp() {
        manejador = new ManejadorExcepcionesGlobal();
    }

    @Test
    @DisplayName("Debe manejar ListaReproduccionNoEncontradaExcepcion retornando 404")
    void manejarExcepcionListaNoEncontrada() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarExcepcionListaNoEncontrada(
                new ListaReproduccionNoEncontradaExcepcion("MiLista")
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getStatus()).isEqualTo(404);
        assertThat(respuesta.getBody().getMessage()).contains("MiLista");
    }

    @Test
    @DisplayName("Debe manejar ListaReproduccionYaExisteExcepcion retornando 409")
    void manejarExcepcionListaYaExiste() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarExcepcionListaYaExiste(
                new ListaReproduccionYaExisteExcepcion("MiLista")
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getStatus()).isEqualTo(409);
    }

    @Test
    @DisplayName("Debe manejar IllegalArgumentException retornando 400")
    void manejarExcepcionArgumentoIlegal() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarExcepcionArgumentoIlegal(
                new IllegalArgumentException("Argumento inválido")
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getStatus()).isEqualTo(400);
        assertThat(respuesta.getBody().getMessage()).isEqualTo("Argumento inválido");
    }

    @Test
    @DisplayName("Debe manejar AccessDeniedException retornando 403")
    void manejarExcepcionAccesoDenegado() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarExcepcionAccesoDenegado(
                new AccessDeniedException("Acceso prohibido")
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getStatus()).isEqualTo(403);
    }

    @Test
    @DisplayName("Debe manejar AuthenticationException retornando 401")
    void manejarExcepcionAutenticacion() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarExcepcionAutenticacion(
                new BadCredentialsException("Clave errónea")
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getStatus()).isEqualTo(401);
    }

    @Test
    @DisplayName("Debe manejar NoResourceFoundException retornando 404")
    void manejarExcepcionRecursoNoEncontrado() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarExcepcionRecursoNoEncontrado(
                new NoResourceFoundException(HttpMethod.GET, "/ruta-inexistente")
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getStatus()).isEqualTo(404);
        assertThat(respuesta.getBody().getMessage()).contains("/ruta-inexistente");
    }

    @Test
    @DisplayName("Debe manejar Exception genérica no controlada retornando 500")
    void manejarExcepcionGenerica() {
        ResponseEntity<RespuestaError> respuesta = manejador.manejarExcepcionGenerica(
                new RuntimeException("Error inesperado")
        );

        assertThat(respuesta.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(respuesta.getBody()).isNotNull();
        assertThat(respuesta.getBody().getStatus()).isEqualTo(500);
    }
}
