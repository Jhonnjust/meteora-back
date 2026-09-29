package com.meteora.backend.dto.response;

import com.meteora.backend.model.OrderItem;

import java.math.BigDecimal;

public record OrderItemResponse(
        Long produtoId,
        String nomeProduto,
        BigDecimal precoUnitario,
        Integer quantidade
) {
    public static OrderItemResponse from(OrderItem i) {
        return new OrderItemResponse(i.getProduto().getId(), i.getNomeProduto(), i.getPrecoUnitario(), i.getQuantidade());
    }
}
