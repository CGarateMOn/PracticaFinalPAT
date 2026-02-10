package edu.comillas.icai.gitt.pat.spring.mvc.models;

import java.time.LocalDate;
import java.util.List;

public record Disponibilidad(
        String idPista,
        LocalDate fecha,
        List<TramosHorarios> tramosHorariosDisponibles
) {}
