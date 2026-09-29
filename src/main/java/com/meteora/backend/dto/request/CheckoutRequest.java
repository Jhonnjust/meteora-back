package com.meteora.backend.dto.request;

import com.meteora.backend.model.PaymentMethod;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record CheckoutRequest(
        @Valid @NotNull AddressRequest endereco,
        @NotNull PaymentMethod formaPagamento,
        /** Numero de cartao fake apenas para simular a integracao (nunca usar em producao real) */
        String numeroCartaoSimulado,
        String cupom
) {}
