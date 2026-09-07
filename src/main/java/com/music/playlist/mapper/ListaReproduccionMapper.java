package com.music.playlist.mapper;

import com.music.playlist.dto.response.RespuestaCancion;
import com.music.playlist.dto.response.RespuestaListaReproduccion;
import com.music.playlist.entity.ListaReproduccion;
import com.music.playlist.entity.ListaReproduccionCancion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring", uses = {CancionMapper.class})
public interface ListaReproduccionMapper {

    @Mapping(target = "canciones", source = "cancionesLista")
    RespuestaListaReproduccion aRespuesta(ListaReproduccion listaReproduccion);

    List<RespuestaListaReproduccion> aListaRespuestas(List<ListaReproduccion> listasReproduccion);

    default RespuestaCancion cancionListaARespuestaCancion(ListaReproduccionCancion relacion) {
        if (relacion == null || relacion.getCancion() == null) {
            return null;
        }
        return new RespuestaCancion(
                relacion.getCancion().getTitulo(),
                relacion.getCancion().getArtista(),
                relacion.getCancion().getAlbum(),
                relacion.getCancion().getAnno(),
                relacion.getCancion().getGenero()
        );
    }
}
