package com.ibk.clientesapi.controller;

import com.ibk.clientesapi.dto.ClienteCreateRequest;
import com.ibk.clientesapi.dto.ClienteDetailResponse;
import com.ibk.clientesapi.dto.ClienteListItemResponse;
import com.ibk.clientesapi.dto.ClienteUpdateRequest;
import com.ibk.clientesapi.service.ClienteService;
import com.ibk.clientesapi.tracing.TraceContext;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

import static org.mockito.ArgumentMatchers.any;

@WebFluxTest(ClienteController.class)
class ClienteControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private ClienteService service;

    @Test
    void debeCrearCliente() {
        Mockito.when(service.crear(Mockito.any(ClienteCreateRequest.class), Mockito.any(TraceContext.class))).thenReturn(Mono.just(new ClienteDetailResponse(
                "1", "Juan", "Perez", "Lopez", OffsetDateTime.now(), true
        )));

        webTestClient.post().uri("/clientes")
                .header("consumerId", "SMP")
                .header("traceparent", "00-db65adadcc7ab67b6eaa38521c34c42a-c1d2e415b56c412b-01")
                .header("deviceType", "IOS")
                .header("deviceId", "device-1")
                .bodyValue(new ClienteCreateRequest("Juan", "Perez", "Lopez", true))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isEqualTo("1")
                .jsonPath("$.nombre").isEqualTo("Juan");
    }

    @Test
    void debeListarClientes() {
        Mockito.when(service.listar(Mockito.any(TraceContext.class))).thenReturn(Flux.just(new ClienteListItemResponse("1", "Juan Perez Lopez")));

        webTestClient.get().uri("/clientes")
                .header("consumerId", "SMP")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$[0].id").isEqualTo("1")
                .jsonPath("$[0].nombreCompleto").isEqualTo("Juan Perez Lopez");
    }

    @Test
    void debeActualizarCliente() {
        Mockito.when(service.actualizar(Mockito.eq("1"), Mockito.any(ClienteUpdateRequest.class), Mockito.any(TraceContext.class))).thenReturn(Mono.just(new ClienteDetailResponse(
                "1", "Juan", "Perez", "Lopez", OffsetDateTime.now(), false
        )));

        webTestClient.put().uri("/clientes/1")
                .header("consumerId", "SMP")
                .bodyValue(new ClienteUpdateRequest("Juan", "Perez", "Lopez", false))
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.activo").isEqualTo(false);
    }
}



