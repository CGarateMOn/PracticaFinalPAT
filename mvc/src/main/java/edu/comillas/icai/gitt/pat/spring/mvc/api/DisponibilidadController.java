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
/*
    @GetMapping("/availability")
    public ResponseEntity<List<Disponibilidad>> getDisponibilidadGeneral(
            @RequestParam String date,
            @RequestParam(required = false) Long courtId){
            logger.info("Consulta de disponibilidad general: fecha={}, pista={}", date, courtId);
            //  Validar formato de fecha (Error 400)
            LocalDate fechaParsed;
            try {
                fechaParsed = LocalDate.parse(date);
            } catch (DateTimeParseException e) {
                logger.error("Error 400: Fecha mal formateada -> {}", date);
                return ResponseEntity.badRequest().body("Formato de fecha inválido. Use YYYY-MM-DD");
            }

            // Si pasan un courtId, validar que la pista exista (Error 404)
            if (courtId != null && !PistatePista(courtId)) {
                logger.warn("Error 404: La pista {} no existe", courtId);
                return ResponseEntity.status(404).build();
            }

            // 3. Obtener resultados
            var disponibilidad = PistaController.consultar(fechaParsed, courtId);
            return ResponseEntity.ok(disponibilidad);
    }
*/
}

