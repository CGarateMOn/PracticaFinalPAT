package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.ModeloUsuarioIncorrecto;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import jakarta.validation.Valid;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
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
            @Valid @RequestBody Usuario usuario,
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
        // 2. Validación de email duplicado
        for(Usuario u : usuarios.values() ) {
            if (u.email().equals(usuario.email())) {
                logger.warn("El email " + usuario.email() +" ya lo están utilizando");
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
            }
        }
        // 3. Creación del usuario forzando el Rol USER
        // Usamos los datos del 'usuario' recibido pero inyectamos el Rol manual
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

//    @PostMapping("/login")
//    public ResponseEntity<Usuario> login() {
//        // Obtenemos el nombre del usuario que acaba de loguearse
//        String nombreUsuario = SecurityContextHolder.getContext().getAuthentication().getName();
//
//        // Lo buscamos en nuestro mapa de datos
//        Usuario usuario = usuarios.get(nombreUsuario);
//
//        if (usuario != null) {
//            logger.info("Login exitoso para: {}", nombreUsuario);
//            return ResponseEntity.ok(usuario); // Retorna 200 OK y el usuario (como pide la foto)
//        }
//
//        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build(); // 401 si algo falla
//    }

    @GetMapping("/me")
    public ResponseEntity<Usuario> me(){
        String nombre = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarios.get(nombre);

        if(usuario != null){
            return ResponseEntity.ok(usuario);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

    }



}
