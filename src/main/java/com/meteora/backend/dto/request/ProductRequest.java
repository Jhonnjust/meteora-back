package com.meteora.backend.dto.request;

import jakarta.validation.constraints.*;

import java.math.BigDecimal;

public record ProductRequest(
        @NotBlank String nome,
        String descricao,
        @NotNull @DecimalMin(value = "0.0", inclusive = false) BigDecimal preco,
        String imagemUrl,
        @NotNull @Min(0) Integer estoque,
        @NotNull Long categoriaId,
        Boolean ativo
) {}
