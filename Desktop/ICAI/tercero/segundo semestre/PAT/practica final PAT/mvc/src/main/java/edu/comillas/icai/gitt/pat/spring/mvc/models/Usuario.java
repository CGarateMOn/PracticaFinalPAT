package edu.comillas.icai.gitt.pat.spring.mvc.models;

import java.time.LocalDateTime;

public record Usuario(
        String idUsuario,
        String nombre,
        String apellidos,
        String email,
        String password,
        String telefono,
        Rol rol,
        LocalDateTime fechaRegistro,
        boolean activo
) {}
