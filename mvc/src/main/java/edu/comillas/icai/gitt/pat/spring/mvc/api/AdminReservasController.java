package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Reserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Comparator;
import java.util.List;

import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.reservas;

@RestController
@RequestMapping("/pistaPadel/admin")
public class AdminReservasController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping("/reservations")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> listarReservasAdmin(
            @RequestParam(required = false) String date,
            @RequestParam(required = false) String courtId,
            @RequestParam(required = false) String userId
    ) {
        // 1) Parseo y validación de date (si viene)
        final LocalDate localDate;
        if (date == null || date.isBlank()) {
            localDate = null;
        } else {
            try {
                localDate = LocalDate.parse(date); // YYYY-MM-DD
            } catch (DateTimeParseException e) {
                logger.warn("ADMIN reservas -> 400. date inválida: '{}'", date);
                return ResponseEntity.badRequest().build();
            }
        }

        // Normalizamos filtros vacíos a null (para simplificar los filters)
        final String filtroCourtId = (courtId == null || courtId.isBlank()) ? null : courtId;
        final String filtroUserId  = (userId == null || userId.isBlank()) ? null : userId;

        logger.info("ADMIN reservas: date={} courtId={} userId={}", localDate, filtroCourtId, filtroUserId);

        // 2) Filtrado en memoria
        List<Reserva> resultado = reservas.values().stream()
                .filter(r -> localDate == null || r.fechaReserva().toLocalDate().equals(localDate))
                .filter(r -> filtroCourtId == null || r.idPista().equals(filtroCourtId))
                .filter(r -> filtroUserId == null || r.idUsuario().equals(filtroUserId))
                .sorted(Comparator
                        .comparing(Reserva::fechaReserva)
                        .thenComparing(Reserva::horaInicio)
                        .thenComparing(Reserva::idPista))
                .toList();

        logger.debug("ADMIN reservas -> 200. resultados={}", resultado.size());
        return ResponseEntity.ok(resultado);
    }
}
