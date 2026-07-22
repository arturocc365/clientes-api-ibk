package com.ibk.clientesapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ClienteUpdateRequest(
        @NotBlank(message = "El nombre no puede ser nulo ni vacío") String nombre,
        @NotBlank(message = "El apellido paterno no puede ser nulo ni vacío") String apellidoPaterno,
        @NotNull(message = "El apellido materno no puede ser nulo") String apellidoMaterno,
        Boolean activo
) {}

