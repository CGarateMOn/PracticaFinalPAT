package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Reserva;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.pistas;
import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.reservas;

@RestController
@RequestMapping("/reservations") // Simplificado para que coincida con tus métodos
public class ReservasController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    // Usamos los mapas de AlmacenDatos para que sea persistente entre controladores

    // 0. CREAR RESERVA
    @PostMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Reserva> crearReserva(
            @Valid @RequestBody Reserva nuevaReserva,
            Authentication authentication
    ) {
        if (!pistas.containsKey(nuevaReserva.idPista())) {
            logger.warn("Pista no encontrada: {}", nuevaReserva.idPista());
            return ResponseEntity.notFound().build();
        }

        LocalTime horaFin = nuevaReserva.horaInicio().plusMinutes(nuevaReserva.duracionMinutos());

        for (Reserva r : reservas.values()) {
            if (r.estado()
                    && r.idPista().equals(nuevaReserva.idPista())
                    && r.fechaReserva().toLocalDate().equals(nuevaReserva.fechaReserva().toLocalDate())) {

                boolean solapan = nuevaReserva.horaInicio().isBefore(r.horaFin())
                        && r.horaInicio().isBefore(horaFin);

                if (solapan) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }

        String reservationId;
        do {
            reservationId = UUID.randomUUID().toString();
        } while (reservas.containsKey(reservationId));

        Reserva reservaCreada = new Reserva(
                reservationId,
                authentication.getName(),
                nuevaReserva.idPista(),
                nuevaReserva.fechaReserva(),
                nuevaReserva.horaInicio(),
                nuevaReserva.duracionMinutos(),
                horaFin,
                true,
                LocalDateTime.now()
        );

        reservas.put(reservationId, reservaCreada);
        return ResponseEntity.status(HttpStatus.CREATED).body(reservaCreada);
    }

    // 1. LISTAR RESERVAS
    @GetMapping
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<List<Reserva>> listarMisReservas(
            @RequestParam(required = false) String from,
            @RequestParam(required = false) String to,
            Authentication authentication
    ) {
        String userId = authentication.getName();
        LocalDate fromDate = (from != null) ? LocalDate.parse(from) : null;
        LocalDate toDate = (to != null) ? LocalDate.parse(to) : null;

        List<Reserva> resultado = reservas.values().stream()
                .filter(r -> r.idUsuario().equals(userId))
                .filter(r -> {
                    LocalDate fechaR = r.fechaReserva().toLocalDate();
                    if (fromDate != null && fechaR.isBefore(fromDate)) return false;
                    if (toDate != null && fechaR.isAfter(toDate)) return false;
                    return true;
                })
                .sorted(Comparator.comparing(Reserva::fechaReserva).thenComparing(Reserva::horaInicio))
                .toList();

        return ResponseEntity.ok(resultado);
    }

    // 2. OBTENER UNA RESERVA
    @GetMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Reserva> obtenerReserva(@PathVariable String reservationId, Authentication authentication) {
        Reserva r = reservas.get(reservationId);
        if (r == null) return ResponseEntity.notFound().build();
        if (!esAdmin(authentication) && !r.idUsuario().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        return ResponseEntity.ok(r);
    }

    // 3. CANCELAR RESERVA
    @DeleteMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Void> cancelarReserva(@PathVariable String reservationId, Authentication authentication) {
        Reserva r = reservas.get(reservationId);
        if (r == null) return ResponseEntity.notFound().build();
        if (!esAdmin(authentication) && !r.idUsuario().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        LocalDateTime momentoInicio = LocalDateTime.of(r.fechaReserva().toLocalDate(), r.horaInicio());
        if (momentoInicio.isBefore(LocalDateTime.now())) return ResponseEntity.status(HttpStatus.CONFLICT).build();

        if (r.estado()) {
            Reserva cancelada = new Reserva(r.idReserva(), r.idUsuario(), r.idPista(), r.fechaReserva(),
                    r.horaInicio(), r.duracionMinutos(), r.horaFin(), false, r.fechaCreacion());
            reservas.put(reservationId, cancelada);
        }
        return ResponseEntity.noContent().build();
    }

    // 4. MODIFICAR RESERVA
    @PatchMapping("/{reservationId}")
    @PreAuthorize("hasAnyRole('USER', 'ADMIN')")
    public ResponseEntity<Reserva> modificarReserva(
            @PathVariable String reservationId,
            @RequestBody Reserva datosActualizar,
            Authentication authentication
    ) {
        Reserva actual = reservas.get(reservationId);
        if (actual == null) return ResponseEntity.notFound().build();
        if (!esAdmin(authentication) && !actual.idUsuario().equals(authentication.getName())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        String nuevaPista = (datosActualizar.idPista() != null) ? datosActualizar.idPista() : actual.idPista();
        LocalDateTime nuevaFecha = (datosActualizar.fechaReserva() != null) ? datosActualizar.fechaReserva() : actual.fechaReserva();
        LocalTime nuevaHoraInicio = (datosActualizar.horaInicio() != null) ? datosActualizar.horaInicio() : actual.horaInicio();
        int nuevaDuracion = (datosActualizar.duracionMinutos() > 0) ? datosActualizar.duracionMinutos() : actual.duracionMinutos();
        LocalTime nuevaHoraFin = nuevaHoraInicio.plusMinutes(nuevaDuracion);

        if (!pistas.containsKey(nuevaPista)) return ResponseEntity.notFound().build();

        for (Reserva r : reservas.values()) {
            if (r.estado() && r.idPista().equals(nuevaPista)
                    && r.fechaReserva().toLocalDate().equals(nuevaFecha.toLocalDate())
                    && !r.idReserva().equals(reservationId)) {
                if (nuevaHoraInicio.isBefore(r.horaFin()) && r.horaInicio().isBefore(nuevaHoraFin)) {
                    return ResponseEntity.status(HttpStatus.CONFLICT).build();
                }
            }
        }

        Reserva actualizada = new Reserva(actual.idReserva(), actual.idUsuario(), nuevaPista, nuevaFecha,
                nuevaHoraInicio, nuevaDuracion, nuevaHoraFin, actual.estado(), actual.fechaCreacion());
        reservas.put(reservationId, actualizada);
        return ResponseEntity.ok(actualizada);
    }

    private boolean esAdmin(Authentication auth) {
        return auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}
