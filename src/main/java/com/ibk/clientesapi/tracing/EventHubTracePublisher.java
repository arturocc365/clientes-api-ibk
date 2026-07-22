package com.ibk.clientesapi.tracing;

import com.azure.messaging.eventhubs.EventData;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
@Primary
@ConditionalOnBean(EventHubProducerClient.class)
public class EventHubTracePublisher implements TracePublisher {

    private static final Logger log = LoggerFactory.getLogger(EventHubTracePublisher.class);

    private final EventHubProducerClient producer;

    public EventHubTracePublisher(EventHubProducerClient producer) {
        this.producer = producer;
    }

    @Override
    public Mono<Void> publish(String payload) {
        return Mono.fromRunnable(() -> {
                    try {
                        producer.send(List.of(new EventData(payload)));
                        log.debug("TRACE_EVENT published to EventHub");
                    } catch (Exception e) {
                        log.warn("Failed to publish TRACE_EVENT to EventHub: {}", e.getMessage());
                    }
                })
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}
