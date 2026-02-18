package edu.comillas.icai.gitt.pat.spring.mvc.records;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;

public record Reserva(
        String idReserva,
        String idUsuario,
        String idPista,
        LocalDateTime fechaReserva,
        LocalTime horaInicio,
        int duracionMinutos,
        LocalTime horaFin,
        boolean estado,
        LocalDateTime fechaCreacion
) {}
