package com.ibk.clientesapi.tracing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@ConditionalOnProperty(prefix = "app.eventhub", name = "enabled", havingValue = "false", matchIfMissing = true)
public class LoggingTracePublisher implements TracePublisher {
    private static final Logger log = LoggerFactory.getLogger(LoggingTracePublisher.class);

    @Override
    public Mono<Void> publish(String payload) {
        return Mono.fromRunnable(() -> log.info("TRACE_EVENT={}", payload))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
