package com.meteora.backend.dto.request;

import jakarta.validation.constraints.NotBlank;

public record CategoryRequest(
        @NotBlank String nome,
        @NotBlank String slug,
        String imagemUrl
) {}
