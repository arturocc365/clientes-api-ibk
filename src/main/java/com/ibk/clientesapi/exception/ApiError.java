package com.ibk.clientesapi.exception;

import java.time.OffsetDateTime;

public record ApiError(
        String code,
        String message,
        OffsetDateTime timestamp
) {}

