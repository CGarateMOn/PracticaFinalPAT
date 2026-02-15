package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Pista;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/courts")
public class PistaController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // 1. LISTAR PISTAS (GET /pistaPadel/courts)
    @GetMapping
    public ResponseEntity<List<Pista>> listarPistas(
            @RequestParam(required = false) Boolean active) {
        logger.trace("se han listado las pistas, según la solicitud haya sido courts?active=true/false debe haber obtenido las pistas active o inactive ");
        return ResponseEntity.ok(Arrays.asList());
    }

    // 2. DETALLE DE UNA PISTA (GET /pistaPadel/courts/{courtId})
    @GetMapping("/{courtId}")
    public ResponseEntity<Pista> obtenerDetalle(@PathVariable String courtId) {
        logger.trace("se ha obtenido el detalle de las caracteristicas de una pista");
        return ResponseEntity.ok(new Pista(courtId, "nombrePrueba", "ubicaciónPrueba", 100.00, false, LocalDateTime.now()));
    }

    // 3. CREAR PISTA (POST /pistaPadel/courts)
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')") // Bloquea si no es ADMIN
    public ResponseEntity<Pista> crearPista(@Valid @RequestBody Pista nuevaPista, //Valida que pista se cree correctamente{
                                            BindingResult result
    ){
        if(result.hasErrors()) {
            logger.warn("se ha intentado crear una pista con datos invalidos" + result.getAllErrors());
            return ResponseEntity.badRequest().build();
        }
        logger.info("se ha creado una pista correctamente");
        return ResponseEntity.status(201).body(nuevaPista);
    }

    // 4. MODIFICAR PISTA (PATCH /pistaPadel/courts/{courtId})
    @PatchMapping("/{courtId}")
    @PreAuthorize("hasRole('ADMIN')") // Bloquea si no es ADMIN
    public ResponseEntity<Pista> modificarPista(
            @PathVariable String courtId,
            @Valid @RequestBody Pista datosActualizar, //valido que los campos del cambio son como deben ser
            BindingResult result) {

        if (result.hasErrors()) {
            result.getFieldErrors().forEach(error ->
                    logger.warn("se ha intentado crear una pista con datos invalidos" + result.getAllErrors()));
                    return ResponseEntity.badRequest().build();
        }

        //ahora quiero asegurarme que no se cambia el id de la pista
        if (datosActualizar.idPista() != null && !datosActualizar.idPista().equals(courtId)) {
            logger.error("Se he intentado modificar el ID de una pista");
            return ResponseEntity.badRequest().build();
        }

        logger.trace("se ha modificado una pista");
        return ResponseEntity.ok(datosActualizar);
    }

    // 5. ELIMINAR PISTA (DELETE /pistaPadel/courts/{courtId})
    @DeleteMapping("/{courtId}")
    @PreAuthorize("hasRole('ADMIN')") // Bloquea 3 si no es ADMIN
    public ResponseEntity<Void> eliminarPista(@PathVariable String courtId) {
        logger.trace("se ha eliminado una pista");
        return ResponseEntity.noContent().build();
    }
}
