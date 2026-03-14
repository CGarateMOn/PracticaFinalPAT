package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoToken;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class UsuarioService {
    @Autowired
    private RepoPistas pistaRepo;
    @Autowired
    private RepoReserva reservaRepo;
    @Autowired
    private RepoUsuarios usuarioRepo;
    @Autowired
    private RepoToken tokenRepo;

    public Usuario Autentica(String password) {
        Optional<Usuario> usuario = usuarioRepo.findByPassword(password);
        if (!usuario.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales no encontrados");
        }
        return usuario.get();
    }

    public Usuario AutenticaAdmin(String password) {
        Optional<Usuario> usuario = usuarioRepo.findByPassword(password);
        if (!usuario.isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales no encontrados");
        }
        if(!usuario.get().getRol().equals(Rol.ADMIN)){
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "No es administrador");
        }
        return usuario.get();
    }

    public Token login(String email, String password){
        Usuario usuario = usuarioRepo.findByEmail(email);
        if(usuario == null) return null;

        Token token = tokenRepo.findByUsuario(usuario);
        if(token != null) return token;

        token = new Token();
        token.usuario= usuario;
        return tokenRepo.save(token);
    }
}
