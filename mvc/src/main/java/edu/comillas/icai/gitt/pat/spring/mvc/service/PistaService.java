package edu.comillas.icai.gitt.pat.spring.mvc.service;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.EstadoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import edu.comillas.icai.gitt.pat.spring.mvc.records.PistaPatchForm;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoPistas;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoReserva;
import edu.comillas.icai.gitt.pat.spring.mvc.repositorios.RepoUsuarios;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class PistaService {
    @Autowired
    private RepoPistas pistaRepo;
    @Autowired
    private RepoReserva reservaRepo;
    @Autowired
    private RepoUsuarios usuarioRepo;

    public Pista crearPista(Pista pista) {
        if(pistaRepo.existsByNombre(pista.getNombre())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Pista ya creada");
        }
        return pistaRepo.save(pista);
    }

    public List<Pista> getTodas() {
        return pistaRepo.findAll();
    }

    public List<Pista> getActivas(Boolean activa) {
        return pistaRepo.findByActiva(activa);
    }

    public Pista getById(Long pistaId) {
        Optional<Pista> pista = pistaRepo.findById(pistaId);
        if (!pista.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }
        return pista.get();
    }

    @Transactional
    public Pista modificarPista(Long pistaId, PistaPatchForm pistaForm) {
        Optional<Pista> pista = pistaRepo.findById(pistaId);
        if (!pista.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }
        if(pistaForm.activa()!=null){
            pista.get().setActiva(pistaForm.activa());
        }
        if(pistaForm.precioHora()!=null){
            pista.get().setPrecioHora(pistaForm.precioHora());
        }
        if(pistaForm.ubicacion()!=null){
            pista.get().setUbicacion(pistaForm.ubicacion());
        }
        if(pistaForm.nombre()!=null){
            //Dos pistas no deberian poder llamarse igual
            if(pistaRepo.existsByNombre(pistaForm.nombre())) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Pista ya creada");
            }
            pista.get().setNombre(pistaForm.nombre());
        }
        return  pistaRepo.save(pista.get());
    }

    @Transactional
    public void eliminaPista(Long pistaId) {
        Optional<Pista> pista = pistaRepo.findById(pistaId);
        if (!pista.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Pista no encontrada");
        }
        if (reservaRepo.existsByPistaAndFechaReservaAfterAndEstado(
                pista.get(), LocalDate.now(), EstadoReserva.ACTIVA)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT,
                    "La pista tiene reservas futuras activas");
        }
        pistaRepo.deleteById(pistaId);
    }
}
