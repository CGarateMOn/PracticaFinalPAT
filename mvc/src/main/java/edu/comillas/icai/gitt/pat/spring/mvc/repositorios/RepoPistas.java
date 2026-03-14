package edu.comillas.icai.gitt.pat.spring.mvc.repositorios;

import edu.comillas.icai.gitt.pat.spring.mvc.entidades.Pista;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface RepoPistas extends CrudRepository<Pista, Long> {
    // GET /courts?active=true/false → filtrar por estado
    List<Pista> findByActiva(Boolean activa);

    // GET /courts?active=true/false → si no se filtra
    List<Pista> findAll();

    // POST /courts → verificar nombre duplicado (409)
    boolean existsByNombre(String nombre);

    // PATCH /courts/{courtId} → verificar nombre duplicado al editar (excluyendo la propia)
    boolean existsByNombreAndIdPistaNot(String nombre, Long idPista);
}
