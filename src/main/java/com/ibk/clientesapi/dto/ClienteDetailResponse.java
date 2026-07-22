package com.ibk.clientesapi.dto;

import java.time.OffsetDateTime;

public record ClienteDetailResponse(
        String id,
        String nombre,
        String apellidoPaterno,
        String apellidoMaterno,
        OffsetDateTime fechaCreacion,
        boolean activo
) {}

