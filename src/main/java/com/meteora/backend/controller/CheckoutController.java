package com.meteora.backend.controller;

import com.meteora.backend.dto.request.CheckoutRequest;
import com.meteora.backend.dto.response.OrderResponse;
import com.meteora.backend.model.Order;
import com.meteora.backend.model.User;
import com.meteora.backend.service.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/** Finalizacao de compra: endereco de entrega, calculo de frete/desconto e pagamento (mock) */
@RestController
@RequestMapping("/api/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<OrderResponse> finalizar(@AuthenticationPrincipal User usuario,
                                                     @Valid @RequestBody CheckoutRequest req) {
        Order pedido = checkoutService.finalizarCompra(usuario, req);
        return ResponseEntity.status(HttpStatus.CREATED).body(OrderResponse.from(pedido));
    }
}
