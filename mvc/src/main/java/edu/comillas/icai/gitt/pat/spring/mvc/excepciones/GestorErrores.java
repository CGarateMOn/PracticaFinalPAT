package edu.comillas.icai.gitt.pat.spring.mvc.excepciones;

import edu.comillas.icai.gitt.pat.spring.mvc.records.ModeloCampoIncorrecto;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@RestControllerAdvice
public class GestorErrores {

    /**
     * Captura las excepciones de validación (400 Bad Request).
     * Se dispara automáticamente cuando un @RequestBody anotado con @Valid falla.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ModeloCampoIncorrecto> manejarValidacion(MethodArgumentNotValidException ex) {
        // Extraemos los errores de campo y los mapeamos a tu record ModeloCampoIncorrecto
        return ex.getBindingResult().getFieldErrors().stream()
                .map(error -> new ModeloCampoIncorrecto(
                        error.getDefaultMessage(),
                        error.getField(),
                        // Convertimos el valor rechazado a String para evitar problemas de tipos
                        error.getRejectedValue() != null ? error.getRejectedValue().toString() : "null"
                ))
                .toList();
    }

    /**
     * Ejemplo de cómo capturar otros errores, como cuando falta un parámetro
     * o hay un error de lógica de negocio que tú lances manualmente.
     */
    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ModeloCampoIncorrecto manejarArgumentoInvalido(IllegalArgumentException ex) {
        return new ModeloCampoIncorrecto(ex.getMessage(), "global", "n/a");
    }
}