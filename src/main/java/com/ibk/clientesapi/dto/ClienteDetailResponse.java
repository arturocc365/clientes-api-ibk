package com.ibk.clientesapi.dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.OffsetDateTime;

public record ClienteDetailResponse(
        String id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ssXXX")
        OffsetDateTime fechaCreacion,
        boolean activo
) {}
