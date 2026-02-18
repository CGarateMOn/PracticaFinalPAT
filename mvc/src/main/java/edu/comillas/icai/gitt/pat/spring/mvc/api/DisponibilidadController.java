package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos;
import edu.comillas.icai.gitt.pat.spring.mvc.records.TramosHorarios;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeParseException;
import java.util.List;

import static org.springframework.http.ResponseEntity.ok;

@RestController
@RequestMapping("/pistaPadel")
public class DisponibilidadController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Horario del club (temporal hardcodeado)
    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    private static final TramosHorarios TRAMO_COMPLETO = new TramosHorarios(HORA_APERTURA, HORA_CIERRE);

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailabilityByDate(
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "courtId", required = false) String courtId)
    {
        if (date == null || date.isBlank()) {
            return ResponseEntity.badRequest().build(); // 404
        }
        final LocalDate localDate;
        try {
            localDate = LocalDate.parse(date); // espera YYYY-MM-DD
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build(); // 400
        }

        // Si viene courtId: devolvemos lista de 1 si existe; si no existe -> lista vacía (200)
        if (courtId != null && !courtId.isBlank()) {
            if (!AlmacenDatos.pistas.containsKey(courtId)) {
                return ResponseEntity.ok(List.of());
            }
            Disponibilidad d = new Disponibilidad(courtId, localDate, List.of(TRAMO_COMPLETO));
            return ResponseEntity.ok(List.of(d));
        }

        // Si NO viene courtId: devolvemos disponibilidad para todas las pistas
        List<Disponibilidad> result = AlmacenDatos.pistas.keySet().stream()
                .map(id -> new Disponibilidad(id, localDate, List.of(TRAMO_COMPLETO)))
                .toList();

        return ResponseEntity.ok(result);
    }

    @GetMapping("/courts/{courtId}/availability")
    public ResponseEntity<?> getCourtAvailability(
            @PathVariable String courtId,
            @RequestParam(name = "date", required = false) String date)
        {
        if (date == null || date.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        final LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        if (!AlmacenDatos.pistas.containsKey(courtId)) {
            return ResponseEntity.notFound().build(); // 404
        }

        logger.info("Disponibilidad pista {} fecha {}", courtId, localDate);

        Disponibilidad disponibilidad = new Disponibilidad(courtId, localDate, List.of(TRAMO_COMPLETO));
        return ResponseEntity.ok(disponibilidad);
    }

}

