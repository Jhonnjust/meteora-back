package com.meteora.backend.dto.response;

import com.meteora.backend.model.Product;

import java.math.BigDecimal;

public record ProductResponse(
        Long id,
        String nome,
        String descricao,
        BigDecimal preco,
        String imagemUrl,
        Integer estoque,
        boolean disponivel,
        CategoryResponse categoria
) {
    public static ProductResponse from(Product p) {
        return new ProductResponse(
                p.getId(),
                p.getNome(),
                p.getDescricao(),
                p.getPreco(),
                p.getImagemUrl(),
                p.getEstoque(),
                p.isAtivo() && p.getEstoque() > 0,
                CategoryResponse.from(p.getCategoria())
        );
    }
}
