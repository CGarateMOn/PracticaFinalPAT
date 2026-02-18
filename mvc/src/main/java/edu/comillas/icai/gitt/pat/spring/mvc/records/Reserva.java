package edu.comillas.icai.gitt.pat.spring.mvc.records;

import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record Reserva(
        @NotBlank(message = "El id no puede estar vacío")
        String idReserva,

        @NotBlank(message = "El id no puede estar vacío")
        String idUsuario,

        @NotBlank(message = "El id no puede estar vacío")
        String idPista,

        @NotNull(message = "La fecha de reserva es obligatoria")
        @FutureOrPresent(message = "La fecha de reserva no puede ser en el pasado")
        LocalDateTime fechaReserva,

        @NotNull(message = "La hora de inicio es obligatoria")
        LocalTime horaInicio,

        @Positive(message = "La duración debe ser positiva")
        int duracionMinutos,

        LocalTime horaFin,
        boolean estado,

        @PastOrPresent(message = "La fecha de creación no puede ser futura")
        LocalDateTime fechaCreacion
) {}
