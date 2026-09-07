package com.music.playlist.mapper;

import com.music.playlist.dto.request.SolicitudCancion;
import com.music.playlist.dto.response.RespuestaCancion;
import com.music.playlist.entity.Cancion;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CancionMapper {

    @Mapping(target = "id", ignore = true)
    Cancion aEntidad(SolicitudCancion solicitud);

    RespuestaCancion aRespuesta(Cancion cancion);
}
