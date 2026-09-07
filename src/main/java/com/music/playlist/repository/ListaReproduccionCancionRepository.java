package com.music.playlist.repository;

import com.music.playlist.entity.ListaReproduccionCancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ListaReproduccionCancionRepository extends JpaRepository<ListaReproduccionCancion, Long> {

    List<ListaReproduccionCancion> findByListaReproduccionId(Long listaReproduccionId);

    void deleteByListaReproduccionId(Long listaReproduccionId);
}
