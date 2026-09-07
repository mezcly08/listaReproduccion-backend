package com.music.playlist.controller;

import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
import com.music.playlist.service.ListaReproduccionServicio;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/lists")
@RequiredArgsConstructor
public class ListaReproduccionControlador {

    private final ListaReproduccionServicio listaReproduccionServicio;

    @PostMapping
    public ResponseEntity<RespuestaListaReproduccion> crearListaReproduccion(@Valid @RequestBody SolicitudListaReproduccion solicitud) {
        RespuestaListaReproduccion creada = listaReproduccionServicio.crearListaReproduccion(solicitud);

        URI ubicacion = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{listName}")
                .buildAndExpand(creada.getNombre())
                .toUri();
        return ResponseEntity.created(ubicacion).body(creada);
    }

    @GetMapping
    public ResponseEntity<List<RespuestaListaReproduccion>> obtenerTodasListasReproduccion() {
        List<RespuestaListaReproduccion> listas = listaReproduccionServicio.obtenerTodasListasReproduccion();
        return ResponseEntity.ok(listas);
    }

    @GetMapping("/{listName}")
    public ResponseEntity<RespuestaListaReproduccion> obtenerListaReproduccionPorNombre(
            @PathVariable("listName") String nombreLista) {
        RespuestaListaReproduccion lista = listaReproduccionServicio.obtenerListaReproduccionPorNombre(nombreLista);
        return ResponseEntity.ok(lista);
    }

    @DeleteMapping("/{listName}")
    public ResponseEntity<Void> eliminarListaReproduccionPorNombre(
            @PathVariable("listName") String nombreLista) {
        listaReproduccionServicio.eliminarListaReproduccionPorNombre(nombreLista);
        return ResponseEntity.noContent().build();
    }
}
