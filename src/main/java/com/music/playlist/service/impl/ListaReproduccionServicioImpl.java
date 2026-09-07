package com.music.playlist.service.impl;

import com.music.playlist.dto.request.SolicitudCancion;
import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
import com.music.playlist.entity.Cancion;
import com.music.playlist.entity.ListaReproduccion;
import com.music.playlist.exception.ListaReproduccionNoEncontradaExcepcion;
import com.music.playlist.exception.ListaReproduccionYaExisteExcepcion;
import com.music.playlist.mapper.CancionMapper;
import com.music.playlist.mapper.ListaReproduccionMapper;
import com.music.playlist.repository.CancionRepository;
import com.music.playlist.repository.ListaReproduccionRepository;
import com.music.playlist.service.ListaReproduccionServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ListaReproduccionServicioImpl implements ListaReproduccionServicio {

    private final ListaReproduccionRepository listaReproduccionRepository;
    private final CancionRepository cancionRepository;
    private final ListaReproduccionMapper listaReproduccionMapper;
    private final CancionMapper cancionMapper;

    @Override
    @Transactional
    public RespuestaListaReproduccion crearListaReproduccion(SolicitudListaReproduccion solicitud) {
        String nombreLista = solicitud.getNombre();
        if (nombreLista == null || nombreLista.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre de la lista de reproducción no puede ser null, vacío ni contener solo espacios");
        }

        if (listaReproduccionRepository.existsByNombre(nombreLista)) {
            throw new ListaReproduccionYaExisteExcepcion(nombreLista);
        }

        ListaReproduccion listaReproduccion = ListaReproduccion.builder()
                .nombre(nombreLista)
                .descripcion(solicitud.getDescripcion())
                .cancionesLista(new ArrayList<>())
                .build();

        if (solicitud.getCanciones() != null && !solicitud.getCanciones().isEmpty()) {
            for (SolicitudCancion cancionSolicitud : solicitud.getCanciones()) {
                Cancion cancion = cancionRepository.findByTituloAndArtista(cancionSolicitud.getTitulo(), cancionSolicitud.getArtista())
                        .orElseGet(() -> cancionRepository.save(cancionMapper.aEntidad(cancionSolicitud)));

                listaReproduccion.agregarCancion(cancion);
            }
        }

        ListaReproduccion listaGuardada = listaReproduccionRepository.save(listaReproduccion);
        return listaReproduccionMapper.aRespuesta(listaGuardada);
    }

    @Override
    public List<RespuestaListaReproduccion> obtenerTodasListasReproduccion() {
        return listaReproduccionMapper.aListaRespuestas(listaReproduccionRepository.findAll());
    }

    @Override
    public RespuestaListaReproduccion obtenerListaReproduccionPorNombre(String nombreLista) {
        ListaReproduccion listaReproduccion = listaReproduccionRepository.findByNombre(nombreLista)
                .orElseThrow(() -> new ListaReproduccionNoEncontradaExcepcion(nombreLista));
        return listaReproduccionMapper.aRespuesta(listaReproduccion);
    }

    @Override
    @Transactional
    public void eliminarListaReproduccionPorNombre(String nombreLista) {
        ListaReproduccion listaReproduccion = listaReproduccionRepository.findByNombre(nombreLista)
                .orElseThrow(() -> new ListaReproduccionNoEncontradaExcepcion(nombreLista));
        listaReproduccionRepository.delete(listaReproduccion);
    }
}
