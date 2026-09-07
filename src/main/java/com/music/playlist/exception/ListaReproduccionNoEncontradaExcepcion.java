package com.music.playlist.exception;

public class ListaReproduccionNoEncontradaExcepcion extends RuntimeException {

    public ListaReproduccionNoEncontradaExcepcion(String nombreLista) {
        super("La lista de reproducción con nombre '" + nombreLista + "' no existe");
    }
}
