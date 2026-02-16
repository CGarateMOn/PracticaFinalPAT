package edu.comillas.icai.gitt.pat.spring.mvc;

import edu.comillas.icai.gitt.pat.spring.mvc.api.ReservasController;
import edu.comillas.icai.gitt.pat.spring.mvc.api.UsuarioController;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service //@Component sirve para que Spring registre la clase, vea que tiene métodos programados y
// activa el reloj interno para ejecutarlos cuando corresponda. ta,bien sepuede poner @Service hace
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
}
