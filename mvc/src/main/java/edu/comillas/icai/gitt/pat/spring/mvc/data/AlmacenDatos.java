package edu.comillas.icai.gitt.pat.spring.mvc.data;

import edu.comillas.icai.gitt.pat.spring.mvc.records.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlmacenDatos {

    public static final Map<String, Pista> pistas = new ConcurrentHashMap<>();
    public static final Map<String, Usuario> usuarios = new ConcurrentHashMap<>();
    public static final Map<String, Reserva> reservas = new ConcurrentHashMap<>();
    public static final Map<String, Disponibilidad> disponibilidad = new ConcurrentHashMap<>();

    static {
        // --- ROLES ---
        Rol rolAdmin = new Rol("1", "ADMIN", "Administrador total del sistema");
        Rol rolUser = new Rol("2", "USER", "Cliente estándar para reservas");

        // --- USUARIOS ---
        // Usuarios Administradores
        usuarios.put("admin01", new Usuario(
                "admin01", "Ana", "García", "admin@padel.com",
                "cifrado123", "600111222", rolAdmin, LocalDateTime.now().minusMonths(1), true));

        usuarios.put("admin02", new Usuario(
                "admin02", "Carlos", "Sanz", "carlos@padel.com",
                "cifrado789", "600999888", rolAdmin, LocalDateTime.now(), true));

        // Usuarios Estándar (Clientes)
        usuarios.put("user01", new Usuario(
                "user01", "Juan", "Pérez", "juan@gmail.com",
                "cifrado456", "611222333", rolUser, LocalDateTime.now().minusDays(10), true));

        usuarios.put("user02", new Usuario(
                "user02", "Marta", "López", "marta.l@gmail.com",
                "claveMarta", "622333444", rolUser, LocalDateTime.now(), true));

        usuarios.put("user03", new Usuario(
                "user03", "Luis", "Gómez", "luis.g@hotmail.com",
                "claveLuis", "633444555", rolUser, LocalDateTime.now(), false)); // Usuario inactivo

        // --- PISTAS ---
        pistas.put("1", new Pista("1", "Pista Central (Cristal)", "Interior", 10.0, true, LocalDateTime.now()));
        pistas.put("2", new Pista("2", "Pista Exterior 1 (Muro)", "Exterior", 8.0, true, LocalDateTime.now()));
        pistas.put("3", new Pista("3", "Pista VIP (Panorámica)", "Interior", 15.0, true, LocalDateTime.now()));
        pistas.put("4", new Pista("4", "Pista Entrenamiento", "Exterior", 5.0, false, LocalDateTime.now())); // No disponible

        // --- DISPONIBILIDAD (Para hoy y mañana) ---
        LocalDate hoy = LocalDate.now();
        LocalDate mañana = hoy.plusDays(1);

        List<TramosHorarios> tramosManana = List.of(
                new TramosHorarios(LocalTime.of(8, 0), LocalTime.of(9, 30), true),
                new TramosHorarios(LocalTime.of(9, 30), LocalTime.of(11, 0), true),
                new TramosHorarios(LocalTime.of(11, 0), LocalTime.of(12, 30), true),
                new TramosHorarios(LocalTime.of(12, 30), LocalTime.of(14, 0), true)
        );

        List<TramosHorarios> tramosTardeNoche = List.of(
                new TramosHorarios(LocalTime.of(15, 30), LocalTime.of(17, 0), true),
                new TramosHorarios(LocalTime.of(17, 0), LocalTime.of(18, 30), true),
                new TramosHorarios(LocalTime.of(18, 30), LocalTime.of(20, 0), true),
                new TramosHorarios(LocalTime.of(20, 0), LocalTime.of(21, 30), true),
                new TramosHorarios(LocalTime.of(21, 30), LocalTime.of(23, 0), true)
        );
        // Disponibilidad para Pista 1 y 2 hoy y mañana
        disponibilidad.put("1-" + hoy, new Disponibilidad("1", hoy, tramosManana));
        disponibilidad.put("2-" + hoy, new Disponibilidad("2", hoy, tramosTardeNoche));
        disponibilidad.put("1-" + mañana, new Disponibilidad("1", mañana, tramosManana));
        disponibilidad.put("3-" + hoy, new Disponibilidad("3", hoy, tramosManana));

        // --- RESERVAS ---
        // Reserva pagada de Juan
        reservas.put("reserva1", new Reserva(
                "reserva1", "user01", "1", hoy.atTime(9, 0),
                LocalTime.of(9, 0), 90, LocalTime.of(10, 30),
                true, LocalDateTime.now().minusHours(5)));

        // Reserva pendiente de Marta
        reservas.put("reserva2", new Reserva(
                "reserva2", "user02", "3", hoy.atTime(17, 0),
                LocalTime.of(17, 0), 90, LocalTime.of(18, 30),
                false, LocalDateTime.now().minusMinutes(30)));
    }

    private AlmacenDatos() {}
}