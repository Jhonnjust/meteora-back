package com.meteora.backend.dto.response;

import com.meteora.backend.model.CartItem;

import java.math.BigDecimal;

public record CartItemResponse(
        Long id,
        Long produtoId,
        String nomeProduto,
        String imagemUrl,
        BigDecimal precoUnitario,
        Integer quantidade,
        BigDecimal subtotal,
        Integer estoqueDisponivel
) {
    public static CartItemResponse from(CartItem item) {
        BigDecimal subtotal = item.getProduto().getPreco().multiply(BigDecimal.valueOf(item.getQuantidade()));
        return new CartItemResponse(
                item.getId(),
                item.getProduto().getId(),
                item.getProduto().getNome(),
                item.getProduto().getImagemUrl(),
                item.getProduto().getPreco(),
                item.getQuantidade(),
                subtotal,
                item.getProduto().getEstoque()
        );
    }
}
