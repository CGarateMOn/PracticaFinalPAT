package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/pistaPadel/users")
public class UsuarioController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    //1. LISTADO DE USUARIOS (GET /pistaPadel/users)
    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<Usuario>> listarUsuarios(){
        logger.trace("se ha obtenido un listado de usuarios");
        return ResponseEntity.ok(Collections.emptyList());
    }

    //2. OBTENER UN USUARIO (GET /pistaPadel/users/{userId})
    @GetMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #userId")
    public ResponseEntity<Usuario> obtenerUsuario(@PathVariable String userId) {
        logger.trace("se ha obtenido un usuario");
        return ResponseEntity.ok(new Usuario(userId, "Nombre", "Apellido", "email", "contraseña", "333", null, null, true));
    }

    //3. ACTUALIZAR LOS DATOS (PATCH /pistaPadel/users/{userId})
    @PatchMapping("/{userId}")
    @PreAuthorize("hasRole('ADMIN') or authentication.name == #userId")
    public ResponseEntity<Usuario> actualizarUsuario(
            @PathVariable String userId,
            @Valid @RequestBody Usuario datosActualizar,
            BindingResult result) {
        if(result.hasErrors()) {
            result.getAllErrors().forEach(error -> logger.error("Se ha intentado modificar el usuario pero los datos instroducidos no son correctos"+ error.getDefaultMessage()));
            return ResponseEntity.badRequest().body(null);
        }

        //quiero evitar que el usuario sea capaz de cambiar su propio id
        //para ello compruebo que el jason que devuelve contiene el mismo id
        if (datosActualizar.idUsuario() != null && !datosActualizar.idUsuario().equals(userId)){
            logger.trace("se ha intentado modificar el id de un usuario, deberia fallar la petición");
            return ResponseEntity.badRequest().build();}

        logger.trace("se ha actualizado un usuario sin alterar su id");
        return ResponseEntity.ok(datosActualizar);
    }
}
