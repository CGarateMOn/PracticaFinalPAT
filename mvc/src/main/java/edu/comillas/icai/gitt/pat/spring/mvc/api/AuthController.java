package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.LoginRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.ProfileResponse;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.RegisterRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.service.AuthService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
@RequestMapping("/pistaPadel/auth")
public class AuthController {

    @Autowired
    AuthService authService;

    private final Logger logger = LoggerFactory.getLogger(getClass());


    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfileResponse register(
            @Valid @RequestBody RegisterRequest register){
        try{
            return authService.registrarUsuario(register);
        }catch (DataIntegrityViolationException e){
            throw new ResponseStatusException(HttpStatus.CONFLICT, e.getMessage(),e);
        }
    }

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> me(@CookieValue(value = "session", required = true) String session){
        if(session==null){
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }

        Usuario usuario = authService.authentication(session);
        if(usuario == null){
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token no válido");
        }

        return  ResponseEntity.ok(ProfileResponse.fromUsuario(usuario));
    }

    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@Valid @RequestBody LoginRequest loginRequest ){
        Token token = authService.login(loginRequest.email(),loginRequest.password());
        if(token == null){
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        ResponseCookie session = ResponseCookie
                .from("session", token.id)
                .httpOnly(true)
                .path("/")
                .sameSite("Strict")
                .build();
        return  ResponseEntity.status(HttpStatus.CREATED).header(HttpHeaders.SET_COOKIE, session.toString()).build();

    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@CookieValue(value = "session", required = true) String session){
        if(session == null){
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED, "No autenticado");
        }
        Usuario usuario = authService.authentication(session);
        if(usuario == null){
            throw  new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }
        authService.logout(session);
        ResponseCookie expireSession = ResponseCookie
                .from("session")
                .httpOnly(true)
                .path("/")
                .maxAge(0)
                .sameSite("Strict")
                .build();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).header(HttpHeaders.SET_COOKIE, expireSession.toString()).build();
    }



}
