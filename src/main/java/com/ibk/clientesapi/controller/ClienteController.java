package com.ibk.clientesapi.controller;

import com.ibk.clientesapi.dto.ClienteCreateRequest;
import com.ibk.clientesapi.dto.ClienteDetailResponse;
import com.ibk.clientesapi.dto.ClienteListItemResponse;
import com.ibk.clientesapi.dto.ClienteUpdateRequest;
import com.ibk.clientesapi.service.ClienteService;
import com.ibk.clientesapi.tracing.TraceContext;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/clientes")
public class ClienteController {
    private final ClienteService service;

    public ClienteController(ClienteService service) {
        this.service = service;
    }

    @PostMapping
    public Mono<ClienteDetailResponse> crear(
            @RequestHeader("consumerId") String consumerId,
            @RequestHeader(value = "traceparent", required = false) String traceparent,
            @RequestHeader(value = "deviceType", required = false) String deviceType,
            @RequestHeader(value = "deviceId", required = false) String deviceId,
            @Valid @RequestBody ClienteCreateRequest request) {
        return service.crear(request, new TraceContext(consumerId, traceparent, deviceType, deviceId));
    }

    @GetMapping
    public Flux<ClienteListItemResponse> listar(
            @RequestHeader("consumerId") String consumerId,
            @RequestHeader(value = "traceparent", required = false) String traceparent,
            @RequestHeader(value = "deviceType", required = false) String deviceType,
            @RequestHeader(value = "deviceId", required = false) String deviceId) {
        return service.listar(new TraceContext(consumerId, traceparent, deviceType, deviceId));
    }

    @PutMapping("/{id}")
    public Mono<ClienteDetailResponse> actualizar(
            @PathVariable String id,
            @RequestHeader("consumerId") String consumerId,
            @RequestHeader(value = "traceparent", required = false) String traceparent,
            @RequestHeader(value = "deviceType", required = false) String deviceType,
            @RequestHeader(value = "deviceId", required = false) String deviceId,
            @Valid @RequestBody ClienteUpdateRequest request) {
        return service.actualizar(id, request, new TraceContext(consumerId, traceparent, deviceType, deviceId));
    }
}


