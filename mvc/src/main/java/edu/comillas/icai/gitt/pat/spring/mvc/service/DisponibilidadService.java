package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Disponibilidad;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.records.TramosHorarios;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class DisponibilidadService {

    private static final LocalTime APERTURA = LocalTime.of(9, 0);
    private static final LocalTime CIERRE = LocalTime.of(22, 0);

    public boolean existePista(String courtId) {
        return AlmacenDatos.pistas.containsKey(courtId);
    }

    public Disponibilidad obtenerDisponibilidadPista(String courtId, LocalDate fecha) {
        List<TramosHorarios> tramos = calcularTramosDisponibles(courtId, fecha);
        return new Disponibilidad(courtId, fecha, tramos);
    }

    public List<Disponibilidad> obtenerDisponibilidadFecha(LocalDate fecha, String courtId) {

        if (courtId != null && !courtId.isBlank()) {
            if (!existePista(courtId)) {
                return new ArrayList<>();
            }
            List<Disponibilidad> resultado = new ArrayList<>();
            resultado.add(obtenerDisponibilidadPista(courtId, fecha));
            return resultado;
        }

        List<Disponibilidad> resultado = new ArrayList<>();
        for (String id : AlmacenDatos.pistas.keySet()) {
            resultado.add(obtenerDisponibilidadPista(id, fecha));
        }
        return resultado;
    }

    private List<TramosHorarios> calcularTramosDisponibles(String courtId, LocalDate fecha) {

        List<Reserva> reservasDelDia = new ArrayList<>();

        // Filtrar reservas de esa pista y fecha
        for (Reserva reserva : AlmacenDatos.reservas.values()) {
            if (reserva.idPista().equals(courtId) &&
                    reserva.fechaReserva().toLocalDate().equals(fecha)) {
                reservasDelDia.add(reserva);
            }
        }

        // 2Ordenar por horaInicio
        reservasDelDia.sort(Comparator.comparing(Reserva::horaInicio));

        List<TramosHorarios> huecos = new ArrayList<>();
        LocalTime cursor = APERTURA;

        // Generar huecos restando reservas
        for (Reserva reserva : reservasDelDia) {

            LocalTime inicioOcupado = reserva.horaInicio();
            LocalTime finOcupado = reserva.horaFin();

            // Ajustar al horario del club
            if (inicioOcupado.isBefore(APERTURA)) {
                inicioOcupado = APERTURA;
            }
            if (finOcupado.isAfter(CIERRE)) {
                finOcupado = CIERRE;
            }

            if (!inicioOcupado.isBefore(finOcupado)) {
                continue;
            }

            // Si hay hueco antes de la reserva
            if (cursor.isBefore(inicioOcupado)) {
                agregarHuecoAjustado(huecos, cursor, inicioOcupado);
            }

            // Mover cursor
            if (cursor.isBefore(finOcupado)) {
                cursor = finOcupado;
            }
        }

        // Hueco final
        if (cursor.isBefore(CIERRE)) {
            agregarHuecoAjustado(huecos, cursor, CIERRE);
        }

        return huecos;
    }

    // Ajustar a múltiplos de 30 y mínimo 30 minutos
    private void agregarHuecoAjustado(List<TramosHorarios> lista, LocalTime inicio, LocalTime fin) {

        LocalTime inicioAjustado = ceilTo30(inicio);
        LocalTime finAjustado = floorTo30(fin);

        if (inicioAjustado.isBefore(finAjustado)) {
            long minutos = Duration.between(inicioAjustado, finAjustado).toMinutes();
            if (minutos >= 30) {
                lista.add(new TramosHorarios(inicioAjustado, finAjustado));
            }
        }
    }

    private LocalTime ceilTo30(LocalTime time) {
        int minuto = time.getMinute();
        if (minuto == 0 || minuto == 30) {
            return time.withSecond(0).withNano(0);
        }
        if (minuto < 30) {
            return time.withMinute(30).withSecond(0).withNano(0);
        }
        return time.plusHours(1).withMinute(0).withSecond(0).withNano(0);
    }

    private LocalTime floorTo30(LocalTime time) {
        int minuto = time.getMinute();
        if (minuto < 30) {
            return time.withMinute(0).withSecond(0).withNano(0);
        }
        return time.withMinute(30).withSecond(0).withNano(0);
    }
}
