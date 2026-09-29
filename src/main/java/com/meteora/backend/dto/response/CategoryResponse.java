package com.meteora.backend.dto.response;

import com.meteora.backend.model.Category;

public record CategoryResponse(
        Long id,
        String nome,
        String slug,
        String imagemUrl
) {
    public static CategoryResponse from(Category c) {
        return new CategoryResponse(c.getId(), c.getNome(), c.getSlug(), c.getImagemUrl());
    }
}
