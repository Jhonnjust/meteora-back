package com.meteora.backend.dto.response;

import com.meteora.backend.model.Order;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderResponse(
        Long id,
        String status,
        String formaPagamento,
        BigDecimal subtotal,
        BigDecimal desconto,
        BigDecimal frete,
        BigDecimal total,
        String detalhesPagamento,
        LocalDateTime criadoEm,
        List<OrderItemResponse> itens
) {
    public static OrderResponse from(Order o) {
        return new OrderResponse(
                o.getId(),
                o.getStatus().name(),
                o.getFormaPagamento().name(),
                o.getSubtotal(),
                o.getDesconto(),
                o.getFrete(),
                o.getTotal(),
                o.getDetalhesPagamento(),
                o.getCriadoEm(),
                o.getItens().stream().map(OrderItemResponse::from).toList()
        );
    }
}
