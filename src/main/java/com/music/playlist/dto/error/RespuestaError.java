package com.music.playlist.dto.error;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@Builder
@Schema(description = "Estructura estandarizada para respuestas de error de la API")
public class RespuestaError {

    @Schema(description = "Código de estado HTTP", example = "404")
    private int status;

    @Schema(description = "Mensaje descriptivo del error", example = "La lista de reproducción 'Rock' no existe")
    private String message;

    @Schema(description = "Fecha y hora en que ocurrió el error", example = "2026-09-07T10:15:30")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    @Schema(description = "Detalles adicionales o lista de errores de validación de campos")
    private List<String> details;

    public RespuestaError() {
        this.timestamp = LocalDateTime.now();
    }

    public RespuestaError(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public RespuestaError(int status, String message, List<String> details) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.details = details;
    }
}
