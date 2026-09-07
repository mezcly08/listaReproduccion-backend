package com.music.playlist.config;

import com.music.playlist.entity.Cancion;
import com.music.playlist.entity.ListaReproduccion;
import com.music.playlist.repository.CancionRepository;
import com.music.playlist.repository.ListaReproduccionRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@Profile("!test")
public class ConfiguracionInicializadorDatos {

    @Bean
    public CommandLineRunner inicializarDatos(ListaReproduccionRepository listaReproduccionRepository, CancionRepository cancionRepository) {
        return args -> {
            if (listaReproduccionRepository.count() == 0) {
                Cancion cancion1 = cancionRepository.save(new Cancion("Bohemian Rhapsody", "Queen", "A Night at the Opera", "1975", "Rock"));
                Cancion cancion2 = cancionRepository.save(new Cancion("Hotel California", "Eagles", "Hotel California", "1976", "Rock"));

                ListaReproduccion rockClassics = new ListaReproduccion("Rock Classics", "Las mejores canciones de rock clásico de todos los tiempos");
                rockClassics.agregarCancion(cancion1);
                rockClassics.agregarCancion(cancion2);

                listaReproduccionRepository.save(rockClassics);
            }
        };
    }
}
