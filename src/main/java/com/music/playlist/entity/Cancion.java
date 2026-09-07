package com.music.playlist.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "canciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = {"titulo", "artista"})
public class Cancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 150)
    private String artista;

    @Column(length = 150)
    private String album;

    @Column(length = 10)
    private String anno;

    @Column(length = 100)
    private String genero;

    public Cancion(String titulo, String artista, String album, String anno, String genero) {
        this.titulo = titulo;
        this.artista = artista;
        this.album = album;
        this.anno = anno;
        this.genero = genero;
    }
}
