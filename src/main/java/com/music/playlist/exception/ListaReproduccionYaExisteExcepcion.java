package com.music.playlist.exception;

public class ListaReproduccionYaExisteExcepcion extends RuntimeException {

    public ListaReproduccionYaExisteExcepcion(String nombreLista) {
        super("Ya existe una lista de reproducción con el nombre '" + nombreLista + "'");
    }
}
