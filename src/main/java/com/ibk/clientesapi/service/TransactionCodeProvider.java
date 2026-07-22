package com.ibk.clientesapi.service;

import org.springframework.stereotype.Component;

@Component
public class TransactionCodeProvider {
    public String codigoConsultaCliente() {
        return "002";
    }

    public String codigoRegistroCliente() {
        return "102";
    }
}

