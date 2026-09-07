package com.music.playlist.exception;

import com.music.playlist.dto.error.RespuestaError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

@RestControllerAdvice
public class ManejadorExcepcionesGlobal {

    private static final Logger log = LoggerFactory.getLogger(ManejadorExcepcionesGlobal.class);

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<RespuestaError> manejarExcepcionesValidacion(MethodArgumentNotValidException ex) {
        List<String> errores = ex.getBindingResult().getFieldErrors().stream()
                .map(FieldError::getDefaultMessage)
                .toList();

        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.BAD_REQUEST.value(),
                "Error de validación en los datos de entrada",
                errores
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaError);
    }

    @ExceptionHandler(ListaReproduccionNoEncontradaExcepcion.class)
    public ResponseEntity<RespuestaError> manejarExcepcionListaNoEncontrada(ListaReproduccionNoEncontradaExcepcion ex) {
        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.NOT_FOUND.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaError);
    }

    @ExceptionHandler(ListaReproduccionYaExisteExcepcion.class)
    public ResponseEntity<RespuestaError> manejarExcepcionListaYaExiste(ListaReproduccionYaExisteExcepcion ex) {
        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.CONFLICT.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.CONFLICT).body(respuestaError);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<RespuestaError> manejarExcepcionArgumentoIlegal(IllegalArgumentException ex) {
        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(respuestaError);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<RespuestaError> manejarExcepcionAccesoDenegado(AccessDeniedException ex) {
        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.FORBIDDEN.value(),
                "Acceso denegado: No cuenta con los permisos necesarios para realizar esta acción"
        );
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(respuestaError);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<RespuestaError> manejarExcepcionAutenticacion(AuthenticationException ex) {
        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.UNAUTHORIZED.value(),
                "No autenticado: Se requieren credenciales válidas para acceder a este recurso"
        );
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(respuestaError);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<RespuestaError> manejarExcepcionRecursoNoEncontrado(NoResourceFoundException ex) {
        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.NOT_FOUND.value(),
                "Recurso no encontrado: " + ex.getResourcePath()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(respuestaError);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RespuestaError> manejarExcepcionGenerica(Exception ex) {
        log.error("Error interno no controlado: ", ex);
        RespuestaError respuestaError = new RespuestaError(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ha ocurrido un error interno en el servidor. Por favor contacte al administrador."
        );
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(respuestaError);
    }
}
