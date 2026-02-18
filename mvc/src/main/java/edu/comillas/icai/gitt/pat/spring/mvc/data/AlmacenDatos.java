package edu.comillas.icai.gitt.pat.spring.mvc.data;

import edu.comillas.icai.gitt.pat.spring.mvc.records.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlmacenDatos {

    // "Tablas" en memoria usando ConcurrentHashMap para evitar errores de concurrencia
    public static final Map<String, Pista> pistas = new ConcurrentHashMap<>();
    public static final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();
    public static final Map<String, Reserva> reservas = new ConcurrentHashMap<>();
    public static final Map<String, Disponibilidad> disponibilidad = new ConcurrentHashMap<>();

    static {
        // INICIALIZAR ROLES (Para los usuarios)
        Rol rolAdmin = new Rol("1", "ADMIN", "Administrador");
        Rol rolUser = new Rol("2", "USER", "Usuario estándar");

        // INICIALIZAR USUARIOS
        usuarios.put("admin01", new Usuario(
                "admin01", "Ana", "García", "admin@padel.com",
                "cifrado123", "600111222", rolAdmin, LocalDateTime.now(), true));

        usuarios.put("user01", new Usuario(
                "user01", "Juan", "Pérez", "juan@gmail.com",
                "cifrado456", "611222333", rolUser, LocalDateTime.now(), true));

        // INICIALIZAR PISTAS
        pistas.put("1", new Pista("1", "Pista Central", "Interior", 10.0, true, LocalDateTime.now()));
        pistas.put("2", new Pista("2", "Pista Exterior", "Exterior", 12.0, true, LocalDateTime.now()));

        // INICIALIZAR DISPONIBILIDAD (Asociada a las pistas creadas)
        LocalDate hoy = LocalDate.now();
        List<TramosHorarios> tramosEstandar = List.of(
                new TramosHorarios(LocalTime.of(9, 0), LocalTime.of(10, 0), true),
                new TramosHorarios(LocalTime.of(10, 0), LocalTime.of(11, 0), true),
                new TramosHorarios(LocalTime.of(18, 0), LocalTime.of(19, 0), true)
        );

        disponibilidad.put("1-" + hoy, new Disponibilidad("1", hoy, tramosEstandar));
        disponibilidad.put("2-" + hoy, new Disponibilidad("2", hoy, tramosEstandar));

        // INICIALIZAR RESERVAS DE PRUEBA
        // reservaId, usuarioId, pistaId, fecha, horaInicio, duracion, horaFin, pagada, fechaCreacion
        reservas.put("reserva1", new Reserva(
                "reserva1", "user01", "1", hoy.atStartOfDay(), //Convierte LocalDate a LocalDateTime
                LocalTime.of(9, 0), 60, LocalTime.of(10, 0),
                true, LocalDateTime.now()));
    }

    private AlmacenDatos() {
        // Constructor privado para evitar que se creen instancias de esta clase de utilidad
    }
}