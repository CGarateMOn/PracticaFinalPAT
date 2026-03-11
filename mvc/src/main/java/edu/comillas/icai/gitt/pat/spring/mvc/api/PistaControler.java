package edu.comillas.icai.gitt.pat.spring.mvc.api;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Usuario;
import edu.comillas.icai.gitt.pat.spring.mvc.records.PistaPatchForm;
import edu.comillas.icai.gitt.pat.spring.mvc.service.PistaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.ReservaService;
import edu.comillas.icai.gitt.pat.spring.mvc.service.UsuarioService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/pistaPadel")

public class PistaControler {
    @Autowired
    PistaService pistaService;
    @Autowired
    UsuarioService usuarioService;
    @Autowired
    ReservaService reservaService;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @PostMapping("/courts")@ResponseStatus(HttpStatus.CREATED)
    public Pista crearPista(@RequestBody Pista pista,
                            @RequestHeader ("Authorization") String password) {
        Usuario usuario = usuarioService.AutenticaAdmin(password);
        return pistaService.crearPista(pista);
    }

    @GetMapping("/courts")
    public List<Pista> obtenerPistas(@RequestParam(required = false) Boolean activa,
                                     @RequestHeader ("Authorization") String password) {
        Usuario usuario = usuarioService.Autentica(password);
        List<Pista> pistas;
        if (activa!=null) {
            pistas = pistaService.getActivas(activa);
        }else{
            pistas = pistaService.getTodas();
        }
        return pistas;
    }

    @GetMapping("/courts/{courtId}")
    public Pista obtenerPista(@PathVariable("courtId") Long courtId,
                              @RequestHeader ("Authorization") String password) {
        Usuario usuario = usuarioService.Autentica(password);
        return pistaService.getById(courtId);
    }

    @PatchMapping("/courts/{courtId}")
    public Pista modificarPista(@PathVariable("courtId") Long courtId,
                                @RequestBody PistaPatchForm pistaForm,
                                @RequestHeader ("Authorization") String password){
        Usuario usuario = usuarioService.AutenticaAdmin(password);
        return pistaService.modificarPista(courtId, pistaForm);
    }

    @DeleteMapping("/courts/{courtId}")@ResponseStatus(HttpStatus.NO_CONTENT)
    public void EliminarPista(@PathVariable("courtId") Long courtId,
                               @RequestHeader ("Authorization") String password){
        Usuario usuario = usuarioService.AutenticaAdmin(password);
        pistaService.eliminaPista(courtId);
    }

}
