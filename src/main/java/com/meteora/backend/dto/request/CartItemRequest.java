package com.meteora.backend.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CartItemRequest(
        @NotNull Long produtoId,
        @Min(1) Integer quantidade
) {}
