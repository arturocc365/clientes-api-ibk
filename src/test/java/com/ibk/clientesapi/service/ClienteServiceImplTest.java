package com.ibk.clientesapi.service;

import com.ibk.clientesapi.dto.ClienteCreateRequest;
import com.ibk.clientesapi.exception.ClienteNotFoundException;
import com.ibk.clientesapi.model.Cliente;
import com.ibk.clientesapi.repository.ClienteRepository;
import com.ibk.clientesapi.tracing.TraceContext;
import com.ibk.clientesapi.tracing.TracePayloadFactory;
import com.ibk.clientesapi.tracing.TracePublisher;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClienteServiceImplTest {

    @Mock
    private ClienteRepository repository;

    @Mock
    private TracePublisher tracePublisher;

    private ClienteServiceImpl service;

    private final TraceContext traceContext = new TraceContext(
            "SMP",
            "00-db65adadcc7ab67b6eaa38521c34c42a-c1d2e415b56c412b-01",
            "IOS",
            "device-1"
    );

    @BeforeEach
    void setUp() {
        TracePayloadFactory tracePayloadFactory = new TracePayloadFactory(
                new ObjectMapper().registerModule(new JavaTimeModule()),
                "este2"
        );
        service = new ClienteServiceImpl(repository, tracePublisher, tracePayloadFactory, new TransactionCodeProvider());
    }

    @Test
    void crearCliente() {
        when(repository.save(any(Cliente.class))).thenAnswer(invocation -> {
            Cliente c = invocation.getArgument(0);
            return Mono.just(Cliente.existente(c.id(), c.nombre(), c.apellidoPaterno(), c.apellidoMaterno(), c.fechaCreacion(), c.activo()));
        });
        when(tracePublisher.publish(anyString())).thenReturn(Mono.empty());

        ClienteCreateRequest request = new ClienteCreateRequest("Juan", "Perez", "Lopez", true);

        StepVerifier.create(service.crear(request, traceContext))
                .assertNext(resp -> {
                    org.junit.jupiter.api.Assertions.assertNotNull(resp.id());
                    org.junit.jupiter.api.Assertions.assertEquals("Juan", resp.nombre());
                    org.junit.jupiter.api.Assertions.assertEquals("Perez", resp.apellidoPaterno());
                    org.junit.jupiter.api.Assertions.assertEquals("Lopez", resp.apellidoMaterno());
                    org.junit.jupiter.api.Assertions.assertTrue(resp.activo());
                })
                .verifyComplete();
    }

    @Test
    void obtenerPorId_debeRetornarClienteConTracing() {
        Cliente cliente = Cliente.existente("1", "Ana", "Garcia", "Lopez", OffsetDateTime.now(), true);
        when(repository.findById("1")).thenReturn(Mono.just(cliente));
        when(tracePublisher.publish(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(service.obtenerPorId("1", traceContext))
                .assertNext(resp -> {
                    org.junit.jupiter.api.Assertions.assertEquals("1", resp.id());
                    org.junit.jupiter.api.Assertions.assertEquals("Ana", resp.nombre());
                    org.junit.jupiter.api.Assertions.assertTrue(resp.activo());
                })
                .verifyComplete();
    }

    @Test
    void obtenerPorId_debeLanzarExcepcionSiNoExiste() {
        when(repository.findById("99")).thenReturn(Mono.empty());

        StepVerifier.create(service.obtenerPorId("99", traceContext))
                .expectError(ClienteNotFoundException.class)
                .verify();
    }

    @Test
    void eliminar_debeDesactivarClienteConTracing() {
        Cliente cliente = Cliente.existente("1", "Ana", "Garcia", "Lopez", OffsetDateTime.now(), true);
        when(repository.findById("1")).thenReturn(Mono.just(cliente));
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            return Mono.just(c);
        });
        when(tracePublisher.publish(anyString())).thenReturn(Mono.empty());

        StepVerifier.create(service.eliminar("1", traceContext))
                .verifyComplete();
    }

    @Test
    void eliminar_debeErrorSiNoExiste() {
        when(repository.findById("99")).thenReturn(Mono.empty());

        StepVerifier.create(service.eliminar("99", traceContext))
                .expectError(ClienteNotFoundException.class)
                .verify();
    }
}



