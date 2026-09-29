package com.meteora.backend.dto.request;

import jakarta.validation.constraints.NotNull;

public record StockAdjustRequest(
        @NotNull Integer quantidade
) {}
