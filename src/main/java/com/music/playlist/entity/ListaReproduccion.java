package com.music.playlist.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "listas_reproduccion")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(of = "nombre")
@ToString(exclude = "cancionesLista")
public class ListaReproduccion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(length = 500)
    private String descripcion;

    @OneToMany(mappedBy = "listaReproduccion", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<ListaReproduccionCancion> cancionesLista = new ArrayList<>();

    public ListaReproduccion(String nombre, String descripcion) {
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.cancionesLista = new ArrayList<>();
    }

    public void agregarCancion(Cancion cancion) {
        ListaReproduccionCancion relacion = new ListaReproduccionCancion(this, cancion);
        this.cancionesLista.add(relacion);
    }

    public void eliminarCancion(Cancion cancion) {
        this.cancionesLista.removeIf(relacion -> relacion.getCancion().equals(cancion));
    }
}
