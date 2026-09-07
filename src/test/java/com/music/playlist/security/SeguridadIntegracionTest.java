package com.music.playlist.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.playlist.config.ConfiguracionSeguridad;
import com.music.playlist.controller.ListaReproduccionControlador;
import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
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

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ListaReproduccionControlador.class)
@Import({ConfiguracionSeguridad.class, ManejadorExcepcionesGlobal.class})
class SeguridadIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ListaReproduccionServicio listaReproduccionServicio;

    @Test
    @DisplayName("Petición sin autenticación debe retornar 401 Unauthorized")
    void peticionSinAutenticacion_debeRetornar401Unauthorized() throws Exception {
        mockMvc.perform(get("/lists"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status", is(401)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Usuario con rol USER haciendo GET /lists está PERMITIDO (200 OK)")
    void rolUser_haciendoGet_debeEstarPermitido() throws Exception {
        when(listaReproduccionServicio.obtenerTodasListasReproduccion()).thenReturn(List.of());

        mockMvc.perform(get("/lists"))
                .andExpect(status().isOk());
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Usuario con rol USER haciendo POST /lists está DENEGADO (403 Forbidden)")
    void rolUser_haciendoPost_debeRetornar403Forbidden() throws Exception {
        SolicitudListaReproduccion solicitud = new SolicitudListaReproduccion("Jazz", "Smooth Jazz", List.of());

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    @DisplayName("Usuario con rol USER haciendo DELETE /lists/{listName} está DENEGADO (403 Forbidden)")
    void rolUser_haciendoDelete_debeRetornar403Forbidden() throws Exception {
        mockMvc.perform(delete("/lists/Jazz").with(csrf()))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Usuario con rol ADMIN haciendo POST /lists está PERMITIDO (201 Created)")
    void rolAdmin_haciendoPost_debeEstarPermitido() throws Exception {
        SolicitudListaReproduccion solicitud = new SolicitudListaReproduccion("Jazz", "Smooth Jazz", List.of());
        RespuestaListaReproduccion respuesta = new RespuestaListaReproduccion("Jazz", "Smooth Jazz", List.of());

        when(listaReproduccionServicio.crearListaReproduccion(any(SolicitudListaReproduccion.class))).thenReturn(respuesta);

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(username = "admin", roles = {"ADMIN"})
    @DisplayName("Usuario con rol ADMIN haciendo DELETE /lists/{listName} está PERMITIDO (204 No Content)")
    void rolAdmin_haciendoDelete_debeEstarPermitido() throws Exception {
        doNothing().when(listaReproduccionServicio).eliminarListaReproduccionPorNombre("Jazz");

        mockMvc.perform(delete("/lists/Jazz").with(csrf()))
                .andExpect(status().isNoContent());
    }
}
