package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.ModeloUsuarioIncorrecto;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.web.bind.annotation.*;

import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.usuarios;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/register")
    public ResponseEntity<Object> register(
            @Valid @RequestBody Usuario usuario, //Comprobamos si falta algún campo
            BindingResult result
    ){
        // 1. Validación de anotaciones (@Valid)
        if(result.hasErrors()){
            List<ModeloUsuarioIncorrecto> errores = result.getFieldErrors()
                    .stream()
                    .map(e -> new ModeloUsuarioIncorrecto(
                            e.getDefaultMessage(),
                            e.getField(),
                            e.getRejectedValue()
                    ))
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errores);
        }
        // ANALIZAMOS SI EL EMAIL YA EXISTE
        for(Usuario u : usuarios.values() ) {
            if (u.email().equals(usuario.email())) {
                logger.warn("El email " + usuario.email() +" ya lo están utilizando");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
            }
        }
        // CUANDO SE REGRISTRE ALGUIEN POR DEFECTO TIENE QUE SER USUARIO
        Usuario nuevoUsuario = new Usuario(
                usuario.idUsuario(),
                usuario.nombre(),
                usuario.apellidos(),
                usuario.email(),
                usuario.password(),
                usuario.telefono(),
                new Rol("1", "USER", "Rol por defecto"), // <--- Forzamos USER
                LocalDateTime.now(),
                true
        );
        // 4. Guardar y responder
        usuarios.put(nuevoUsuario.idUsuario(), nuevoUsuario);
        logger.info("Gracias {}, tu cuenta se ha creado correctamente", nuevoUsuario.nombre());

        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoUsuario);
    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> me(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarios.values().stream()
                .filter(u -> u.email().equals(email))
                .findFirst()
                .orElse(null);

        if(usuario != null){
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(){
        return me();
    }



}
