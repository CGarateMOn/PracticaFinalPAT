package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.records.TramosHorarios;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ReservaService {

    @Autowired
    private RepoPistas pistaRepo;
    @Autowired
    private RepoReserva reservaRepo;
    @Autowired
    private RepoUsuarios usuarioRepo;

    private static final LocalTime HORA_APERTURA = LocalTime.of(9, 0);
    private static final LocalTime HORA_CIERRE = LocalTime.of(22, 0);

    public List<Disponibilidad> obtenerDisponibilidadPorFecha(LocalDate fecha) {
        List<Pista> pistasActivas = pistaRepo.findByActiva(true);
        List<Disponibilidad> resultado = new ArrayList<>();

        for (Pista pista : pistasActivas) {
            List<Reserva> reservasActivas = reservaRepo.findByPistaAndFechaReservaAndEstado(
                    pista, fecha, EstadoReserva.ACTIVA
            );

            List<TramosHorarios> huecos = calcularHuecosDisponibles(reservasActivas);

            resultado.add(new Disponibilidad(
                    String.valueOf(pista.getIdPista()),
                    fecha,
                    huecos
            ));
        }

        return resultado;
    }

    public Disponibilidad obtenerDisponibilidadPista(Long courtId, LocalDate fecha) {
        Pista pista = pistaRepo.findById(courtId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada"));

        if (!Boolean.TRUE.equals(pista.getActiva())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "La pista no está activa");
        }

        List<Reserva> reservasActivas = reservaRepo.findByPistaAndFechaReservaAndEstado(
                pista, fecha, EstadoReserva.ACTIVA
        );

        List<TramosHorarios> huecos = calcularHuecosDisponibles(reservasActivas);

        return new Disponibilidad(
                String.valueOf(pista.getIdPista()),
                fecha,
                huecos
        );
    }

    private List<TramosHorarios> calcularHuecosDisponibles(List<Reserva> reservas) {
        List<TramosHorarios> huecos = new ArrayList<>();

        reservas.sort(Comparator.comparing(Reserva::getHoraInicio));

        LocalTime cursor = HORA_APERTURA;

        for (Reserva reserva : reservas) {
            LocalTime inicioReserva = reserva.getHoraInicio();
            LocalTime finReserva = reserva.getHoraFin();

            // Si hay hueco entre el cursor y el inicio de la reserva, se añade
            if (cursor.isBefore(inicioReserva)) {
                huecos.add(new TramosHorarios(cursor, inicioReserva));
            }

            // Avanzamos el cursor al final de la reserva si está más adelante
            if (cursor.isBefore(finReserva)) {
                cursor = finReserva;
            }
        }

        // Hueco final hasta cierre
        if (cursor.isBefore(HORA_CIERRE)) {
            huecos.add(new TramosHorarios(cursor, HORA_CIERRE));
        }

        return huecos;
    }
}