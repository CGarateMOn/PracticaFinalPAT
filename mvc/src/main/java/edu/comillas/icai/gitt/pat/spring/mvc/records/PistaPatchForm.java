package edu.comillas.icai.gitt.pat.spring.mvc.records;

import java.math.BigDecimal;

public record PistaPatchForm(
        String nombre,
        String ubicacion,
        BigDecimal precioHora,
        Boolean activa
) {
}
