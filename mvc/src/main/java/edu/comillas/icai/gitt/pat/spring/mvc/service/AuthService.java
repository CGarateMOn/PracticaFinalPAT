package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Token;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.ProfileResponse;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.RegisterRequest;
import edu.comillas.icai.gitt.pat.spring.mvc.modelos.Rol;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoToken;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {
    @Autowired
    RepoUsuarios repoUsuario;
    @Autowired
    RepoToken repoToken;
    @Autowired
    Hashing hashing;

    public ProfileResponse registrarUsuario(RegisterRequest register){
        Usuario usuario = new Usuario();
        usuario.setEmail(register.email());
        usuario.setPassword(hashing.hash(register.password()));
        usuario.setNombre(register.nombre());
        usuario.setApellidos(register.apellidos());
        usuario.setTelefono(register.telefono());

        Usuario usuarioGuardado = repoUsuario.save(usuario);
        return ProfileResponse.fromUsuario(usuarioGuardado);
    }

    public Token login(String email, String password){
        Usuario usuario = repoUsuario.findByEmail(email);
        if(usuario == null) return null;

        if(!hashing.compare(password, usuario.getPassword())) return null;

        Token token = repoToken.findByUsuario(usuario);
        if(token != null) return token;

        token = new Token();
        token.usuario= usuario;
        return repoToken.save(token);
    }

    public Usuario authentication(String tokenId){
        Optional<Token> token = repoToken.findById(tokenId);
        return token.map(value-> value.usuario).orElse(null);
    }

    public void logout(String tokenId){
        Optional<Token> token = repoToken.findById(tokenId);
        if(token.isPresent()){
            repoToken.deleteById(tokenId);
        }
    }
}
