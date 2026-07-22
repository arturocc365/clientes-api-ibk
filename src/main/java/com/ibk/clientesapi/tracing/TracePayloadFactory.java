package com.ibk.clientesapi.tracing;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

@Component
public class TracePayloadFactory {
    private static final ZoneId PERU_ZONE = ZoneId.of("America/Lima");

    private final ObjectMapper objectMapper;
    private final String region;

    public TracePayloadFactory(ObjectMapper objectMapper, @Value("${app.region:este2}") String region) {
        this.objectMapper = objectMapper;
        this.region = region;
    }

    public Mono<String> buildJson(TraceContext context, String customerId, Object inbound, Object outbound, String statusCode, String transactionCode) {
        return Mono.fromCallable(() -> {
                    AnalyticsTracePayload payload = new AnalyticsTracePayload(
                            "application-" + context.consumerId(),
                            context.consumerId(),
                            String.valueOf(System.currentTimeMillis()),
                            context.consumerId(),
                            OffsetDateTime.now(PERU_ZONE).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME),
                            customerId,
                            region,
                            statusCode,
                            System.currentTimeMillis(),
                            extractTraceId(context.traceparent()),
                            objectMapper.writeValueAsString(inbound),
                            objectMapper.writeValueAsString(outbound),
                            transactionCode
                    );
                    return objectMapper.writeValueAsString(payload);
                })
                .subscribeOn(Schedulers.boundedElastic())
                .onErrorMap(JsonProcessingException.class,
                        e -> new IllegalStateException("No se pudo construir el payload de trazabilidad", e));
    }

    private String extractTraceId(String traceparent) {
        if (traceparent == null || traceparent.isBlank()) {
            return "";
        }
        String[] parts = traceparent.split("-");
        return parts.length >= 3 ? parts[1] : traceparent;
    }
}
