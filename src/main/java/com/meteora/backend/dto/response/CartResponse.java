package com.meteora.backend.dto.response;

import java.math.BigDecimal;
import java.util.List;

public record CartResponse(
        List<CartItemResponse> itens,
        BigDecimal total,
        int quantidadeItens
) {}
