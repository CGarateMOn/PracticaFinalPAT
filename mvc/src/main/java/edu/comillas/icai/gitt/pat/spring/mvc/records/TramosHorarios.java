package edu.comillas.icai.gitt.pat.spring.mvc.records;

import java.time.LocalTime;

public record TramosHorarios(
        LocalTime inicio,
        LocalTime fin,
        boolean disponible
)
{}
