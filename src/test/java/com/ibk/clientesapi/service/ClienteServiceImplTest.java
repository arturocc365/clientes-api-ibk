package com.ibk.clientesapi.service;

import com.ibk.clientesapi.dto.ClienteCreateRequest;
import com.ibk.clientesapi.dto.ClienteUpdateRequest;
import com.ibk.clientesapi.exception.ClienteNotFoundException;
import com.ibk.clientesapi.model.Cliente;
import com.ibk.clientesapi.repository.ClienteRepository;
import com.ibk.clientesapi.tracing.TraceContext;
import com.ibk.clientesapi.tracing.TracePayloadFactory;
import com.ibk.clientesapi.tracing.TracePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private TracePublisher tracePublisher;

    @Test
    void crearClienteIncluyeTraceIdYCodigoTransaccion() {
        ClienteServiceImpl service = buildService();
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            return Mono.just(Cliente.existente(c.id(), c.nombre(), c.apellidoPaterno(), c.apellidoMaterno(), c.fechaCreacion(), c.activo()));
        });
        when(tracePublisher.publish(anyString())).thenReturn(Mono.empty());

        ClienteCreateRequest request = new ClienteCreateRequest("Juan", "Perez", "Lopez", true);
        TraceContext traceContext = new TraceContext("SMP", "00-db65adadcc7ab67b6eaa38521c34c42a-c1d2e415b56c412b-01", "IOS", "device-1");

        StepVerifier.create(service.crear(request, traceContext))
                .assertNext(resp -> {
                    org.junit.jupiter.api.Assertions.assertNotNull(resp.id());
                    org.junit.jupiter.api.Assertions.assertEquals("Juan", resp.nombre());
                    org.junit.jupiter.api.Assertions.assertEquals("Perez", resp.apellidoPaterno());
                    org.junit.jupiter.api.Assertions.assertEquals("Lopez", resp.apellidoMaterno());
                    org.junit.jupiter.api.Assertions.assertTrue(resp.activo());
                })
                .verifyComplete();

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(tracePublisher).publish(payloadCaptor.capture());
        String payload = payloadCaptor.getValue();

        assertTrue(payload.contains("\"statusCode\":\"0000\""));
        assertTrue(payload.contains("\"transactionCode\":\"102\""));
        assertTrue(payload.contains("\"traceId\":\"db65adadcc7ab67b6eaa38521c34c42a\""));
    }

    @Test
    void actualizarClienteNoEncontradoPublicaTraza9999() {
        ClienteServiceImpl service = buildService();
        when(repository.findById("404")).thenReturn(Mono.empty());
        when(tracePublisher.publish(anyString())).thenReturn(Mono.empty());

        ClienteUpdateRequest request = new ClienteUpdateRequest("Juan", "Perez", "Lopez", true);
        TraceContext traceContext = new TraceContext("SMP", "00-db65adadcc7ab67b6eaa38521c34c42a-c1d2e415b56c412b-01", "IOS", "device-1");

        StepVerifier.create(service.actualizar("404", request, traceContext))
                .expectErrorSatisfies(ex -> {
                    assertTrue(ex instanceof ClienteNotFoundException);
                    assertTrue(ex.getMessage().contains("Cliente no encontrado"));
                })
                .verify();

        ArgumentCaptor<String> payloadCaptor = ArgumentCaptor.forClass(String.class);
        verify(tracePublisher).publish(payloadCaptor.capture());
        String payload = payloadCaptor.getValue();

        assertTrue(payload.contains("\"statusCode\":\"9999\""));
        assertTrue(payload.contains("\"transactionCode\":\"102\""));
        assertTrue(payload.contains("\"customerId\":\"404\""));
    }

    private ClienteServiceImpl buildService() {
        TracePayloadFactory tracePayloadFactory = new TracePayloadFactory(
                new ObjectMapper().registerModule(new JavaTimeModule()),
                "este2"
        );
        TransactionCodeProvider transactionCodeProvider = new TransactionCodeProvider();
        return new ClienteServiceImpl(repository, tracePublisher, tracePayloadFactory, transactionCodeProvider);
    }
}


