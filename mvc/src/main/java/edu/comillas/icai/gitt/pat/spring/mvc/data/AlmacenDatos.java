package edu.comillas.icai.gitt.pat.spring.mvc.data;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Reserva;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AlmacenDatos {

    // “Tablas” en memoria
    public static final Map<String, Pista> PISTAS = new ConcurrentHashMap<>();
    public static final Map<String, Usuario> USUARIOS = new ConcurrentHashMap<>();
    public static final Map<String, Reserva> RESERVAS = new ConcurrentHashMap<>();

    static {
        // --- PISTAS de ejemplo (para poder devolver 404 si no existe) ---
        PISTAS.put("1", new Pista("1", "Pista 1", "Interior", 10.0, true, LocalDateTime.now()));
        PISTAS.put("2", new Pista("2", "Pista 2", "Exterior", 12.0, true, LocalDateTime.now()));

        // --- Usuarios y reservas: lo dejamos vacío por ahora ---
    }

    private AlmacenDatos() {
        // Evita instanciación
    }
}
