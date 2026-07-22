package com.ibk.clientesapi.repository;

import com.ibk.clientesapi.model.Cliente;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

public interface ClienteRepository extends ReactiveCrudRepository<Cliente, String> {
}

