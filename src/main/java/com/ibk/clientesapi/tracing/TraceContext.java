package com.ibk.clientesapi.tracing;

public record TraceContext(
        String consumerId,
        String traceparent,
        String deviceType,
        String deviceId
) {}

