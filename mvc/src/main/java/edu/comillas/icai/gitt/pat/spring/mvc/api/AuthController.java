package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.records.ModeloUsuarioIncorrecto;
import edu.comillas.icai.gitt.pat.spring.mvc.records.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.seguridad.ExcepcionUsuarioIncorrecto;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static edu.comillas.icai.gitt.pat.spring.mvc.data.AlmacenDatos.USUARIOS;
import static java.util.stream.Collectors.toList;

@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    private final Logger logger = LoggerFactory.getLogger(getClass());



//    @ExceptionHandler(ExcepcionUsuarioIncorrecto.class)
//    @ResponseStatus(HttpStatus.BAD_REQUEST)
//    public List<ModeloUsuarioIncorrecto> usuarioIncorrecto(ExcepcionUsuarioIncorrecto ex) {
//        return ex.getErrores().stream().map(error -> new ModeloUsuarioIncorrecto(
//                error.getDefaultMessage(), error.getField(), error.getRejectedValue()
//        )).toList();
//    }

    @PostMapping("/register")
    public ResponseEntity crea(
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
        for(Usuario u : USUARIOS.values() ){
            if(u.email().equals(usuario.email())){
                return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
            }
        }
        logger.info("Gracias", usuario.nombre(), "tu cuenta se ha creado correctamente");
        USUARIOS.put(usuario.idUsuario(), usuario);
        return ResponseEntity.status(HttpStatus.CREATED).body(usuario);
    }



}
