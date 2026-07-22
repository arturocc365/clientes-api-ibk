package com.ibk.clientesapi.tracing;

import reactor.core.publisher.Mono;

public interface TracePublisher {
    Mono<Void> publish(String payload);
}

