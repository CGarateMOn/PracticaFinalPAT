package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Reserva;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

@RequestMapping("/pistaPadel")
public class ReservasController {
    public static final Map<String, Reserva> reservas = new HashMap<>(); //para poder acceder a los mails y enviar recordatorios
}
