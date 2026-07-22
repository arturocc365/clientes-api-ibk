package com.ibk.clientesapi.service;

import com.ibk.clientesapi.dto.ClienteCreateRequest;
import com.ibk.clientesapi.dto.ClienteDetailResponse;
import com.ibk.clientesapi.dto.ClienteListItemResponse;
import com.ibk.clientesapi.dto.ClienteUpdateRequest;
import com.ibk.clientesapi.exception.ClienteNotFoundException;
import com.ibk.clientesapi.model.Cliente;
import com.ibk.clientesapi.repository.ClienteRepository;
import com.ibk.clientesapi.tracing.TraceContext;
import com.ibk.clientesapi.tracing.TracePayloadFactory;
import com.ibk.clientesapi.tracing.TracePublisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.UUID;

@Service
public class ClienteServiceImpl implements ClienteService {
    private static final ZoneId PERU_ZONE = ZoneId.of("America/Lima");

    private final ClienteRepository repository;
    private final TracePublisher tracePublisher;
    private final TracePayloadFactory tracePayloadFactory;
    private final TransactionCodeProvider transactionCodeProvider;

    public ClienteServiceImpl(ClienteRepository repository,
                              TracePublisher tracePublisher,
                              TracePayloadFactory tracePayloadFactory,
                              TransactionCodeProvider transactionCodeProvider) {
        this.repository = repository;
        this.tracePublisher = tracePublisher;
        this.tracePayloadFactory = tracePayloadFactory;
        this.transactionCodeProvider = transactionCodeProvider;
    }

    @Override
    public Mono<ClienteDetailResponse> crear(ClienteCreateRequest request, TraceContext traceContext) {
        Cliente cliente = Cliente.nuevo(
                UUID.randomUUID().toString(),
                request.nombre(),
                request.apellidoPaterno(),
                request.apellidoMaterno(),
                OffsetDateTime.now(PERU_ZONE),
                request.activo() == null || request.activo()
        );

        return repository.save(cliente)
                .flatMap(saved -> {
                    ClienteDetailResponse response = toDetailResponse(saved);
                    return tracePayloadFactory.buildJson(
                                    traceContext,
                                    saved.id(),
                                    request,
                                    response,
                                    "0000",
                                    transactionCodeProvider.codigoRegistroCliente()
                            )
                            .flatMap(tracePublisher::publish)
                            .thenReturn(response);
                });
    }

    @Override
    public Mono<ClienteDetailResponse> actualizar(String id, ClienteUpdateRequest request, TraceContext traceContext) {
        String notFoundMessage = "Cliente no encontrado: " + id;
        return repository.findById(id)
                .switchIfEmpty(Mono.defer(() -> tracePayloadFactory.buildJson(
                                        traceContext,
                                        id,
                                        request,
                                        Map.of("message", notFoundMessage),
                                        "9999",
                                        transactionCodeProvider.codigoRegistroCliente()
                                )
                                .flatMap(tracePublisher::publish)
                                .then(Mono.error(new ClienteNotFoundException(notFoundMessage)))))
                .flatMap(actual -> {
                    Cliente updated = Cliente.existente(
                            actual.id(),
                            request.nombre(),
                            request.apellidoPaterno(),
                            request.apellidoMaterno(),
                            actual.fechaCreacion(),
                            request.activo() == null || request.activo()
                    );
                    return repository.save(updated)
                            .flatMap(saved -> {
                                ClienteDetailResponse response = toDetailResponse(saved);
                                return tracePayloadFactory.buildJson(
                                                traceContext,
                                                saved.id(),
                                                request,
                                                response,
                                                "0000",
                                                transactionCodeProvider.codigoRegistroCliente()
                                        )
                                        .flatMap(tracePublisher::publish)
                                        .thenReturn(response);
                            });
                });
    }

    @Override
    public Flux<ClienteListItemResponse> listar(TraceContext traceContext) {
        return repository.findAll()
                .map(cliente -> new ClienteListItemResponse(cliente.id(), cliente.nombreCompleto()))
                .concatMap(response -> tracePayloadFactory.buildJson(
                                traceContext,
                                response.id(),
                                "LIST",
                                response,
                                "0000",
                                transactionCodeProvider.codigoConsultaCliente()
                        )
                        .flatMap(tracePublisher::publish)
                        .thenReturn(response));
    }

    @Override
    public Mono<ClienteDetailResponse> obtenerPorId(String id) {
        return repository.findById(id)
                .switchIfEmpty(Mono.error(new ClienteNotFoundException("Cliente no encontrado: " + id)))
                .map(this::toDetailResponse);
    }

    private ClienteDetailResponse toDetailResponse(Cliente cliente) {
        return new ClienteDetailResponse(
                cliente.id(),
                cliente.nombre(),
                cliente.apellidoPaterno(),
                cliente.apellidoMaterno(),
                cliente.fechaCreacion(),
                cliente.activo()
        );
    }
}
