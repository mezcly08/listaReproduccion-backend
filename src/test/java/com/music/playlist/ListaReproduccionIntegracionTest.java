package com.music.playlist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.music.playlist.dto.request.SolicitudCancion;
import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.repository.CancionRepository;
import com.music.playlist.repository.ListaReproduccionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class ListaReproduccionIntegracionTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ListaReproduccionRepository listaReproduccionRepository;

    @Autowired
    private CancionRepository cancionRepository;

    @BeforeEach
    void setUp() {
        listaReproduccionRepository.deleteAll();
        cancionRepository.deleteAll();
    }

    @Test
    @DisplayName("Flujo E2E completo: Crear, Consultar, Validar Duplicados, Compartir Canciones y Eliminar preservando canciones")
    void flujoCompletoGestionListasReproduccion() throws Exception {
        // 1. Consultar inicialmente sin listas registradas -> 200 OK []
        mockMvc.perform(get("/lists")
                        .with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(0)));

        // 2. Intentar crear sin autenticación -> 401 Unauthorized
        SolicitudCancion cancion1 = new SolicitudCancion("Stairway to Heaven", "Led Zeppelin", "Led Zeppelin IV", "1971", "Rock");
        SolicitudCancion cancion2 = new SolicitudCancion("Comfortably Numb", "Pink Floyd", "The Wall", "1979", "Progressive Rock");
        SolicitudListaReproduccion solicitud1 = new SolicitudListaReproduccion("Rock Titans", "Grandes clásicos del rock", List.of(cancion1, cancion2));

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud1)))
                .andExpect(status().isUnauthorized());

        // 3. Intentar crear con rol USER -> 403 Forbidden
        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .with(httpBasic("user", "user123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud1)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status", is(403)));

        // 4. Crear exitosamente como ADMIN -> 201 Created
        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud1)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", containsString("/lists/Rock%20Titans")))
                .andExpect(jsonPath("$.nombre", is("Rock Titans")))
                .andExpect(jsonPath("$.canciones", hasSize(2)));

        // Verificar que las canciones físicas se persistieron en BD
        assertThat(cancionRepository.count()).isEqualTo(2);

        // 5. Consultar todas las listas como USER -> 200 OK con 1 lista
        mockMvc.perform(get("/lists")
                        .with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].nombre", is("Rock Titans")));

        // 6. Consultar por nombre como USER -> 200 OK
        mockMvc.perform(get("/lists/Rock Titans")
                        .with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Rock Titans")))
                .andExpect(jsonPath("$.canciones", hasSize(2)));

        // 7. Intentar crear lista con el mismo nombre -> 409 Conflict
        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud1)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status", is(409)));

        // 8. Crear una segunda lista que reusa "Stairway to Heaven" (no debe duplicar la canción en BD)
        SolicitudCancion cancionExistente = new SolicitudCancion("Stairway to Heaven", "Led Zeppelin", null, null, null);
        SolicitudCancion cancionNueva = new SolicitudCancion("Highway to Hell", "AC/DC", "Highway to Hell", "1979", "Hard Rock");
        SolicitudListaReproduccion solicitud2 = new SolicitudListaReproduccion("Legends", "Más leyendas", List.of(cancionExistente, cancionNueva));

        mockMvc.perform(post("/lists")
                        .with(csrf())
                        .with(httpBasic("admin", "admin123"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(solicitud2)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nombre", is("Legends")))
                .andExpect(jsonPath("$.canciones", hasSize(2)));

        // Deben existir 3 canciones en total en el catálogo (2 anteriores + 1 nueva)
        assertThat(cancionRepository.count()).isEqualTo(3);

        // 9. Eliminar la primera lista ("Rock Titans") como ADMIN -> 204 No Content
        mockMvc.perform(delete("/lists/Rock Titans")
                        .with(csrf())
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isNoContent());

        // 10. Consultar la lista eliminada -> 404 Not Found
        mockMvc.perform(get("/lists/Rock Titans")
                        .with(httpBasic("user", "user123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));

        // 11. CRÍTICO: Las 3 canciones físicas del catálogo siguen intactas
        assertThat(cancionRepository.count()).isEqualTo(3);

        // 12. La segunda lista ("Legends") sigue intacta con sus 2 canciones
        mockMvc.perform(get("/lists/Legends")
                        .with(httpBasic("user", "user123")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre", is("Legends")))
                .andExpect(jsonPath("$.canciones", hasSize(2)));
    }

    @Test
    @DisplayName("Intentar eliminar una lista inexistente debe retornar 404 Not Found")
    void eliminarListaInexistente_debeRetornar404NotFound() throws Exception {
        mockMvc.perform(delete("/lists/NoExiste")
                        .with(csrf())
                        .with(httpBasic("admin", "admin123")))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }
}
