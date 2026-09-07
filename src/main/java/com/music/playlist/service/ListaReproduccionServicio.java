package com.music.playlist.service;

import com.music.playlist.dto.request.SolicitudListaReproduccion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;

import java.util.List;

public interface ListaReproduccionServicio {

    RespuestaListaReproduccion crearListaReproduccion(SolicitudListaReproduccion solicitud);

    List<RespuestaListaReproduccion> obtenerTodasListasReproduccion();

    RespuestaListaReproduccion obtenerListaReproduccionPorNombre(String nombreLista);

    void eliminarListaReproduccionPorNombre(String nombreLista);
}
