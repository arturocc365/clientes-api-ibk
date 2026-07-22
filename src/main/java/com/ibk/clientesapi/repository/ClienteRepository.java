package com.ibk.clientesapi.repository;

import com.ibk.clientesapi.model.Cliente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ClienteRepository extends ReactiveCrudRepository<Cliente, String> {
    Flux<Cliente> findByActivoTrue();
}

