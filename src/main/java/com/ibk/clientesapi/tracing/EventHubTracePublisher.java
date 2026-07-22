package com.ibk.clientesapi.tracing;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerAsyncClient;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
@ConditionalOnProperty(prefix = "app.eventhub", name = "enabled", havingValue = "true")
public class EventHubTracePublisher implements TracePublisher {
    private static final Logger log = LoggerFactory.getLogger(EventHubTracePublisher.class);

    private final EventHubProducerAsyncClient producerClient;

    public EventHubTracePublisher(
            @Value("${app.eventhub.connection-string}") String connectionString,
            @Value("${app.eventhub.name}") String eventHubName
    ) {
        this.producerClient = new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildAsyncProducerClient();
    }

    @Override
    public Mono<Void> publish(String payload) {
        return Mono.fromRunnable(() -> log.info("TRACE_EVENT={}", payload))
                .then(producerClient.send(List.of(new EventData(payload))));
    }

    @PreDestroy
    void close() {
        producerClient.close();
    }
}

