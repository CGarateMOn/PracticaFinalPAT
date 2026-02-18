package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.ModeloUsuarioIncorrecto;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.USUARIOS;

import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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
        if(result.hasErrors()){
            List<ModeloUsuarioIncorrecto> usuarioIncorrecto = result.getFieldErrors()
                    .stream()
                    .map(e -> new ModeloUsuarioIncorrecto(
                            e.getDefaultMessage(),
                            e.getField(),
                            e.getRejectedValue()
                    ))
                    .toList();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(usuarioIncorrecto);
        }
        for(Usuario u : usuarios.values() ){
            if(u.email().equals(usuario.email())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
            }
        }

        Usuario usuario1 = new Usuario(
                usuario.idUsuario(),
                usuario.nombre(),
                usuario.apellidos(),
                usuario.email(),
                usuario.password(),
                usuario.telefono(),
                new Rol("1", "USER", "Rol por defecto"),
                LocalDateTime.now(),
                true
        );
        logger.info("Gracias "+ usuario.nombre()+ " tu cuenta se ha creado correctamente");
        USUARIOS.put(usuario1.idUsuario(), usuario1);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario1);
        logger.info("Gracias", usuario.nombre(), "tu cuenta se ha creado correctamente");
        usuarios.put(usuario.idUsuario(), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }

    @PostMapping("/login")
    public Usuario login(@RequestBody  )





}
