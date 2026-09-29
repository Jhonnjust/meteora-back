package com.meteora.backend.dto.response;

public record AuthResponse(
        String token,
        String tipo,
        Long usuarioId,
        String nome,
        String email,
        String role
) {}
