package edu.comillas.icai.gitt.pat.spring.mvc.records;

public record ModeloCampoIncorrecto(
        String mensaje,
        String campo,
        String valorRechazado
) {}