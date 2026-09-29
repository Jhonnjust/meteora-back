package com.meteora.backend.controller.admin;

import com.meteora.backend.dto.request.OrderStatusUpdateRequest;
import com.meteora.backend.dto.response.OrderResponse;
import com.meteora.backend.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/** Painel administrativo - visualizacao e atualizacao de status dos pedidos (pago, enviado, entregue, cancelado) */
@RestController
@RequestMapping("/api/admin/orders")
@RequiredArgsConstructor
public class AdminOrderController {

    private final OrderService orderService;

    @GetMapping
    public Page<OrderResponse> listar(Pageable pageable) {
        return orderService.listarTodos(pageable).map(OrderResponse::from);
    }

    @GetMapping("/{id}")
    public OrderResponse detalhar(@PathVariable Long id) {
        return OrderResponse.from(orderService.buscarPorId(id));
    }

    @PatchMapping("/{id}/status")
    public OrderResponse atualizarStatus(@PathVariable Long id, @Valid @RequestBody OrderStatusUpdateRequest req) {
        return OrderResponse.from(orderService.atualizarStatus(id, req.status()));
    }
}
