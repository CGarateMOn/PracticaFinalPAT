package edu.comillas.icai.gitt.pat.spring.mvc;

import edu.comillas.icai.gitt.pat.spring.mvc.api.DisponibilidadController;
import edu.comillas.icai.gitt.pat.spring.mvc.api.ReservasController;
import edu.comillas.icai.gitt.pat.spring.mvc.api.UsuarioController;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service //@Component sirve para que Spring registre la clase, vea que tiene métodos programados y
// activa el reloj interno para ejecutarlos cuando corresponda. tambien sepuede poner @Service hace
// lo mismo pero además indica que la clase contiene lógica de negocio
public class TareasProgramadas {

    private final Logger logger = LoggerFactory.getLogger(getClass());
    // Inyectamos el controlador para acceder a sus datos

    @Scheduled(cron = "0 0 2 * * *")
    public void mandarRecordatorioReservas() {
        LocalDate hoy = LocalDate.now();
        logger.info("Iniciando envío de recordatorios (2:00 AM) para el día: {}", hoy);
        // Recorremos el mapa de reservas
        ReservasController.reservas.values().forEach(reserva -> {
            // Comprobamos si la reserva es para hoy
            if (reserva.fechaReserva().equals(hoy)) {
                // Obtenemos el ID del usuario de esa reserva
                String idUsuario = reserva.idUsuario();
                // Buscamos al usuario en el mapa de UsuarioController para obtener el email
                Usuario usuario = UsuarioController.usuarios.get(idUsuario);
                /* RELLENAR CON LÓGICA DE ENVIAR CORREOS Y NO SOLO UN MENSJE EN LA TERMINAL
                if (usuario != null) {
                    enviarEmail(usuario.email(), "Recordatorio de Pista",
                            "Hola " + usuario.nombre() + ", te recordamos tu reserva de hoy.");
                }
             */
            }
        });
    }
    // Información de pistas el día 1 de cada mes a las 09:00 AM, se puede cambiar la hora si se quiere.
    @Scheduled(cron = "0 0 9 1 * *")
    public void mandarInfoMensualPistas() {
        // Construimos un resumen de la disponibilidad actual del mapa
        StringBuilder resumenDisponibilidad = new StringBuilder();
        resumenDisponibilidad.append("Horarios destacados para hoy:\n");

        DisponibilidadController.mapaDisponibilidad.values().forEach(disp -> {
            resumenDisponibilidad.append("- Pista: ").append(disp.idPista()).append("\n");
            disp.tramosHorariosDisponibles().forEach(tramo -> {
                if (tramo.disponible()) {
                    resumenDisponibilidad.append("  * ").append(tramo.inicio()).append(" a ").append(tramo.fin()).append("\n");
                }
            });
        });
        // Enviamos a todos los usuarios registrados
        UsuarioController.usuarios.values().forEach(usuario -> {
            if (usuario.activo()) {
                String cuerpoMensaje = "Hola " + usuario.nombre() + ",\n\n" +
                        "Te enviamos la disponibilidad de pistas para iniciar el mes:\n\n" +
                        resumenDisponibilidad.toString() +
                        "\nReserva ya en nuestra App.";

                logger.info("PARA: {}", usuario);
            }
        });
    }
}
