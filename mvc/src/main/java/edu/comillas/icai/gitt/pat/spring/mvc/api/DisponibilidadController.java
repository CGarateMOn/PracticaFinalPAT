package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

}

