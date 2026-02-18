package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.service.DisponibilidadService;
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
    private final DisponibilidadService disponibilidadService;

    public DisponibilidadController(DisponibilidadService disponibilidadService) {
        this.disponibilidadService = disponibilidadService;
    }

    // GET /pistaPadel/availability?date=YYYY-MM-DD&courtId=...
    @GetMapping("/availability")
    public ResponseEntity<?> getAvailabilityByDate(
            @RequestParam(name = "date", required = false) String date,
            @RequestParam(name = "courtId", required = false) String courtId
    ) {
        LocalDate localDate = parseDate(date);
        if (localDate == null) {
            logger.warn("Disponibilidad (general) -> 400. date ausente o inválida: '{}'", date);
            return ResponseEntity.badRequest().build(); // 400
        }

        logger.info("Disponibilidad (general) fecha={} courtId={}", localDate, courtId);

        // para el endpoint general ponemos que si courtId no existe -> 200 con lista vacía
        // se podría poner 404 pero ya hay endpoint de pista especifico
        List<Disponibilidad> result = disponibilidadService.obtenerDisponibilidadFecha(localDate, courtId);

        logger.debug("Disponibilidad (general) -> 200. resultados={}", result.size());
        return ResponseEntity.ok(result);
    }

    // GET /pistaPadel/courts/{courtId}/availability?date=YYYY-MM-DD
    @GetMapping("/courts/{courtId}/availability")
    public ResponseEntity<?> getCourtAvailability(
            @PathVariable String courtId,
            @RequestParam(name = "date", required = false) String date
    ) {
        LocalDate localDate = parseDate(date);
        if (localDate == null) {
            logger.warn("Disponibilidad (pista) -> 400. courtId={} date ausente o inválida: '{}'", courtId, date);
            return ResponseEntity.badRequest().build(); // 400
        }

        if (!disponibilidadService.existePista(courtId)) {
            logger.warn("Disponibilidad (pista) -> 404. courtId={} no existe", courtId);
            return ResponseEntity.notFound().build(); // 404
        }

        logger.info("Disponibilidad (pista) fecha={} courtId={}", localDate, courtId);

        Disponibilidad disponibilidad = disponibilidadService.obtenerDisponibilidadPista(courtId, localDate);

        logger.debug("Disponibilidad (pista) -> 200. tramos={}",
                disponibilidad.tramosHorariosDisponibles().size());

        return ResponseEntity.ok(disponibilidad);
    }

    private LocalDate parseDate(String date) {
        if (date == null || date.isBlank()) return null;
        try {
            return LocalDate.parse(date); // YYYY-MM-DD
        } catch (DateTimeParseException ex) {
            return null;
        }
    }
}
