package edu.comillas.icai.gitt.pat.spring.mvc.records;

import java.time.LocalDate;
import java.util.List;

public record Disponibilidad(
        String idPista,
        LocalDate fecha,
        List<TramosHorarios> tramosHorariosDisponibles
) {}
