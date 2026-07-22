package com.ibk.clientesapi.config;

import com.azure.messaging.eventhubs.EventHubClientBuilder;
import com.azure.messaging.eventhubs.EventHubProducerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EventHubConfig {

    private static final Logger log = LoggerFactory.getLogger(EventHubConfig.class);

    @Bean
    @ConditionalOnProperty(name = "app.eventhub.connection-string")
    public EventHubProducerClient eventHubProducerClient(
            @Value("${app.eventhub.connection-string}") String connectionString,
            @Value("${app.eventhub.name}") String eventHubName) {
        log.info("Creating EventHubProducerClient for hub: {}", eventHubName);
        return new EventHubClientBuilder()
                .connectionString(connectionString, eventHubName)
                .buildProducerClient();
    }
}
