package edu.comillas.icai.gitt.pat.spring.mvc.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.time.LocalDateTime;

public record Pista(
        @NotBlank(message = "El id no puede estar vacío")
        String idPista,
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,
        @NotBlank(message = "La ubicación no puede estar vacía")
        String ubicacion,
        @NotBlank(message = "El precio hora no puede estar vacío")
        @Positive(message = "El precio hora debe ser mayor a cero")
        Double precioHora,
        boolean activa,

        LocalDateTime fechaAlta
) {}
