package com.ibk.clientesapi.service;

import com.ibk.clientesapi.dto.ClienteCreateRequest;
import com.ibk.clientesapi.dto.ClienteDetailResponse;
import com.ibk.clientesapi.dto.ClienteListItemResponse;
import com.ibk.clientesapi.dto.ClienteUpdateRequest;
import com.ibk.clientesapi.tracing.TraceContext;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ClienteService {
    Mono<ClienteDetailResponse> crear(ClienteCreateRequest request, TraceContext traceContext);
    Mono<ClienteDetailResponse> actualizar(String id, ClienteUpdateRequest request, TraceContext traceContext);
    Flux<ClienteListItemResponse> listar(TraceContext traceContext);
    Mono<ClienteDetailResponse> obtenerPorId(String id);
}


