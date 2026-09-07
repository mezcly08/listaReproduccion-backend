package com.music.playlist.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.playlist.config.ConfiguracionSeguridad;
import com.music.playlist.dto.request.SolicitudCancion;
import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.dto.response.RespuestaCancion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
import com.music.playlist.exception.ListaReproduccionNoEncontradaExcepcion;
import com.music.playlist.exception.ListaReproduccionYaExisteExcepcion;
import com.music.playlist.exception.ManejadorExcepcionesGlobal;
import com.music.playlist.service.ListaReproduccionServicio;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.containsStringIgnoringCase;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ListaReproduccionControlador.class)
@Import({ConfiguracionSeguridad.class, ManejadorExcepcionesGlobal.class})
class ListaReproduccionControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListaReproduccionServicio listaReproduccionServicio;

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /lists con datos válidos debe retornar 201 y Location")
    void crearListaReproduccion_cuandoEsValido_debeRetornar201Created() throws Exception {
        SolicitudCancion cancionSolicitud = new SolicitudCancion("Bohemian Rhapsody", "Queen", "A Night at the Opera", "1975", "Rock");
        SolicitudListaReproduccion solicitud = new SolicitudListaReproduccion("Rock Classics", "Lista de rock", List.of(cancionSolicitud));

        RespuestaCancion cancionRespuesta = new RespuestaCancion("Bohemian Rhapsody", "Queen", "A Night at the Opera", "1975", "Rock");
        RespuestaListaReproduccion respuesta = new RespuestaListaReproduccion("Rock Classics", "Lista de rock", List.of(cancionRespuesta));

        when(listaReproduccionServicio.crearListaReproduccion(any(SolicitudListaReproduccion.class))).thenReturn(respuesta);

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/lists/Rock%20Classics")))
                .andExpect(jsonPath("$.nombre", is("Rock Classics")))
                .andExpect(jsonPath("$.descripcion", is("Lista de rock")))
                .andExpect(jsonPath("$.canciones", hasSize(1)))
                .andExpect(jsonPath("$.canciones[0].titulo", is("Bohemian Rhapsody")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /lists con nombre vacío debe retornar 400 Bad Request")
    void crearListaReproduccion_cuandoNombreVacio_debeRetornar400BadRequest() throws Exception {
        SolicitudListaReproduccion solicitud = new SolicitudListaReproduccion("", "Descripción", List.of());

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Error de validación")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /lists con nombre null debe retornar 400 Bad Request")
    void crearListaReproduccion_cuandoNombreNull_debeRetornar400BadRequest() throws Exception {
        SolicitudListaReproduccion solicitud = new SolicitudListaReproduccion(null, "Descripción", List.of());

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.message", containsString("Error de validación")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /lists con nombre solo espacios debe retornar 400 Bad Request")
    void crearListaReproduccion_cuandoNombreSoloEspacios_debeRetornar400BadRequest() throws Exception {
        SolicitudListaReproduccion solicitud = new SolicitudListaReproduccion("   ", "Descripción", List.of());

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("POST /lists con nombre duplicado debe retornar 409 Conflict")
    void crearListaReproduccion_cuandoNombreDuplicado_debeRetornar409Conflict() throws Exception {
        SolicitudListaReproduccion solicitud = new SolicitudListaReproduccion("Rock Classics", "Descripción", List.of());

        when(listaReproduccionServicio.crearListaReproduccion(any(SolicitudListaReproduccion.class)))
                .thenThrow(new ListaReproduccionYaExisteExcepcion("Rock Classics"));

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)))
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("ya existe")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /lists debe retornar 200 OK con lista de playlists")
    void obtenerTodasListasReproduccion_debeRetornar200OK() throws Exception {
        RespuestaListaReproduccion respuesta = new RespuestaListaReproduccion("Rock Classics", "Descripción", List.of());

        when(listaReproduccionServicio.obtenerTodasListasReproduccion()).thenReturn(List.of(respuesta));

        mockMvc.perform(get("/lists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Rock Classics")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /lists/{listName} existente debe retornar 200 OK")
    void obtenerListaReproduccionPorNombre_cuandoExiste_debeRetornar200OK() throws Exception {
        RespuestaListaReproduccion respuesta = new RespuestaListaReproduccion("Rock Classics", "Descripción", List.of());

        when(listaReproduccionServicio.obtenerListaReproduccionPorNombre("Rock Classics")).thenReturn(respuesta);

        mockMvc.perform(get("/lists/Rock Classics"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Rock Classics")));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("GET /lists/{listName} inexistente debe retornar 404 Not Found")
    void obtenerListaReproduccionPorNombre_cuandoNoExiste_debeRetornar404NotFound() throws Exception {
        when(listaReproduccionServicio.obtenerListaReproduccionPorNombre("NonExistent"))
                .thenThrow(new ListaReproduccionNoEncontradaExcepcion("NonExistent"));

        mockMvc.perform(get("/lists/NonExistent"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("no existe")));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /lists/{listName} existente debe retornar 204 No Content")
    void eliminarListaReproduccionPorNombre_cuandoExiste_debeRetornar204NoContent() throws Exception {
        doNothing().when(listaReproduccionServicio).eliminarListaReproduccionPorNombre("Rock Classics");

        mockMvc.perform(delete("/lists/Rock Classics").with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("DELETE /lists/{listName} inexistente debe retornar 404 Not Found")
    void eliminarListaReproduccionPorNombre_cuandoNoExiste_debeRetornar404NotFound() throws Exception {
        doThrow(new ListaReproduccionNoEncontradaExcepcion("NonExistent"))
                .when(listaReproduccionServicio).eliminarListaReproduccionPorNombre("NonExistent");

        mockMvc.perform(delete("/lists/NonExistent").with(csrf()))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", containsStringIgnoringCase("no existe")));
    }
}
