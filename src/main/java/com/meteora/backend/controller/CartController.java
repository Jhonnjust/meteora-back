package com.meteora.backend.controller;

import com.meteora.backend.dto.request.CartItemRequest;
import com.meteora.backend.dto.response.CartResponse;
import com.meteora.backend.model.User;
import com.meteora.backend.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Carrinho de compras - requer usuario autenticado (ver SecurityConfig) */
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public CartResponse obter(@AuthenticationPrincipal User usuario) {
        return cartService.obterCarrinho(usuario);
    }

    @PostMapping("/itens")
    public CartResponse adicionar(@AuthenticationPrincipal User usuario, @Valid @RequestBody CartItemRequest req) {
        return cartService.adicionarItem(usuario, req);
    }

    @PutMapping("/itens/{produtoId}")
    public CartResponse atualizar(@AuthenticationPrincipal User usuario,
                                   @PathVariable Long produtoId,
                                   @RequestParam int quantidade) {
        return cartService.atualizarQuantidade(usuario, produtoId, quantidade);
    }

    @DeleteMapping("/itens/{produtoId}")
    public CartResponse remover(@AuthenticationPrincipal User usuario, @PathVariable Long produtoId) {
        return cartService.removerItem(usuario, produtoId);
    }

    @DeleteMapping
    public void esvaziar(@AuthenticationPrincipal User usuario) {
        cartService.esvaziar(usuario);
    }
}
