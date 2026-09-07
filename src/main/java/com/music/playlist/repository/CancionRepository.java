package com.music.playlist.repository;

import com.music.playlist.entity.Cancion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CancionRepository extends JpaRepository<Cancion, Long> {

    Optional<Cancion> findByTituloAndArtista(String titulo, String artista);
}
