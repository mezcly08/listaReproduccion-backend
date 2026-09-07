package com.music.playlist.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lista_reproduccion_canciones")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "id")
@ToString(exclude = {"listaReproduccion", "cancion"})
public class ListaReproduccionCancion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lista_reproduccion_id", nullable = false)
    private ListaReproduccion listaReproduccion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cancion_id", nullable = false)
    private Cancion cancion;

    public ListaReproduccionCancion(ListaReproduccion listaReproduccion, Cancion cancion) {
        this.listaReproduccion = listaReproduccion;
        this.cancion = cancion;
    }
}
