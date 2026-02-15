package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/pistaPadel/users")
public class UsuarioController {
    public static final Map<String, Usuario> usuarios = new HashMap<>();

    static {
        Rol rolAdmin = new Rol("1", "ADMIN", "Administrador");
        Rol rolUser = new Rol("2", "USER", "Usuario estándar");

        usuarios.put("admin01", new Usuario("admin01", "Ana", "García", "admin@padel.com", "cifrado123", "600111222", rolAdmin, LocalDateTime.now(), true));
        usuarios.put("user01", new Usuario("user01", "Juan", "Pérez", "juan@gmail.com", "cifrado456", "611222333", rolUser, LocalDateTime.now(), true));
    }

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios(){
        return ResponseEntity.ok(usuarios.values().stream().toList());
    }

    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #userId")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable String userId) {
        if (usuarios.containsKey(userId)) {
            return ResponseEntity.ok(usuarios.get(userId));
        }
        return ResponseEntity.notFound().build();
    }

    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #userId")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable String userId,
            @Valid @RequestBody Usuario datosActualizar,
            BindingResult result) {

        if(result.hasErrors()) return ResponseEntity.badRequest().build();
        if (!usuarios.containsKey(userId)) return ResponseEntity.notFound().build();

        // Evitar cambio de ID
        if (datosActualizar.idUsuario() != null && !datosActualizar.idUsuario().equals(userId)){
            return ResponseEntity.badRequest().header("Error", "No se puede cambiar el ID").build();
        }

        // Actualizamos el mapa para que la prueba en Postman sea persistente en memoria
        usuarios.put(userId, datosActualizar);
        logger.info("Usuario {} actualizado correctamente", userId);
        return ResponseEntity.ok(datosActualizar);
    }
}