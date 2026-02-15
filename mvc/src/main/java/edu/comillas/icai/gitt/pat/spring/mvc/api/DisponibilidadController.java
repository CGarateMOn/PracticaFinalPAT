package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel")
public class DisponibilidadController {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/availability")
    public ResponseEntity<?> getAvailabilityByDate(
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "courtId", required = false) String courtId)
    {
        if (date == null || date.isBlank()) {
            return ResponseEntity.badRequest().build(); // 400
        }

        final LocalDate localDate;
        try {
            localDate = LocalDate.parse(date); // espera YYYY-MM-DD
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build(); // 400
        }

        logger.info("Consulta disponibilidad fecha {} courtId {}", localDate, courtId);

        Disponibilidad disponibilidad = new Disponibilidad(
                courtId,
                localDate,
                List.of()
        );

        return ResponseEntity.ok(disponibilidad);
    }

    @GetMapping("/courts/{courtId}/availability")
    public ResponseEntity<?> getCourtAvailability(
            @PathVariable String courtId,
            @RequestParam(name = "date", required = false) String date
    ) {
        if (date == null || date.isBlank()) {
            return ResponseEntity.badRequest().build();
        }

        final LocalDate localDate;
        try {
            localDate = LocalDate.parse(date);
        } catch (DateTimeParseException ex) {
            return ResponseEntity.badRequest().build();
        }

        // ToDo: aquí más adelante irás a PistaRepository/Service para comprobar existencia real
        boolean pistaExiste = true; // placeholder por ahora

        if (!pistaExiste) {
            return ResponseEntity.notFound().build(); // 404
        }

        logger.info("Disponibilidad pista {} fecha {}", courtId, localDate);

        Disponibilidad disponibilidad = new Disponibilidad(courtId, localDate, List.of());
        return ResponseEntity.ok(disponibilidad);
    }


}

