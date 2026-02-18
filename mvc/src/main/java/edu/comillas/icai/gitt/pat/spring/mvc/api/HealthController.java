package edu.comillas.icai.gitt.pat.spring.mvc.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// este controlador es para verificar que la app no está caida
// y que responde peticions HTTP
// lo suelen usar plataformas de despliegue:
// si no devuelve 200 -> la plataforma considera que la app está caída y no la levanta
@RestController
@RequestMapping("/pistaPadel")
public class HealthController {

    @GetMapping("/health")
    public ResponseEntity<Void> health() {
        return ResponseEntity.ok().build();
    }
}
