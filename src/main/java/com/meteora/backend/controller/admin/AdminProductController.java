package com.meteora.backend.controller.admin;

import com.meteora.backend.dto.request.ProductRequest;
import com.meteora.backend.dto.request.StockAdjustRequest;
import com.meteora.backend.dto.response.ProductResponse;
import com.meteora.backend.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/** Painel administrativo - cadastro/edicao de produtos e ajuste de estoque. Requer ROLE_ADMIN. */
@RestController
@RequestMapping("/api/admin/products")
@RequiredArgsConstructor
public class AdminProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> criar(@Valid @RequestBody ProductRequest req) {
        ProductResponse resp = ProductResponse.from(productService.criar(req));
        return ResponseEntity.status(HttpStatus.CREATED).body(resp);
    }

    @PutMapping("/{id}")
    public ProductResponse atualizar(@PathVariable Long id, @Valid @RequestBody ProductRequest req) {
        return ProductResponse.from(productService.atualizar(id, req));
    }

    @PatchMapping("/{id}/estoque")
    public ProductResponse ajustarEstoque(@PathVariable Long id, @Valid @RequestBody StockAdjustRequest req) {
        return ProductResponse.from(productService.ajustarEstoque(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable Long id) {
        productService.remover(id);
        return ResponseEntity.noContent().build();
    }
}
