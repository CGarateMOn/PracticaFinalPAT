package edu.comillas.icai.gitt.pat.spring.mvc.seguridad;

import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;

import java.util.List;

public class ExcepcionUsuarioIncorrecto extends RuntimeException{
    private List<FieldError> errores;
    public ExcepcionUsuarioIncorrecto(BindingResult result){
        this.errores = result.getFieldErrors();
    }
    public List<FieldError> getErrores(){
        return errores;
    }
}
