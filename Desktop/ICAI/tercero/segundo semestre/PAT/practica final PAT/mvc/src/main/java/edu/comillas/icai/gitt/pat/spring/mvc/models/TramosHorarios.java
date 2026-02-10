package edu.comillas.icai.gitt.pat.spring.mvc.models;

import java.time.LocalTime;

public record TramosHorarios(
        LocalTime inicio,
        LocalTime fin
)
{}
