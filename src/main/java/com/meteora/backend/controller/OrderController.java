package com.meteora.backend.controller;

import com.meteora.backend.dto.response.OrderResponse;
import com.meteora.backend.model.User;
import com.meteora.backend.service.OrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Historico de pedidos do cliente logado (area do cliente) */
@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderResponse> listar(@AuthenticationPrincipal User usuario, Pageable pageable) {
        return orderService.listarDoUsuario(usuario, pageable).map(OrderResponse::from);
    }

    @GetMapping("/{id}")
    public OrderResponse detalhar(@AuthenticationPrincipal User usuario, @PathVariable Long id) {
        return OrderResponse.from(orderService.buscarDoUsuario(usuario, id));
    }
}
