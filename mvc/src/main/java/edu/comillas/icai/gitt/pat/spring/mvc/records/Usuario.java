package edu.comillas.icai.gitt.pat.spring.mvc.records;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public record Usuario(
        @NotBlank(message = "El id no puede estar vacío")
        String idUsuario,
        @NotBlank(message = "El nombre no puede estar vacío")
        String nombre,
        @NotBlank(message = "El apellido no puede estar vacío")
        String apellidos,
        @NotBlank(message = "El email no puede estar vacío")
        String email,
        @NotBlank(message = "La contraseña no puede estar vacío")
        String password,
        @NotBlank(message = "El telefono no puede estar vacío")
        String telefono,
        @NotNull(message = "El objeto Rol no puede ser nulo")
        Rol rol,
        LocalDateTime fechaRegistro,
        boolean activo
) {}
