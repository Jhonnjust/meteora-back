package com.meteora.backend.controller;

import com.meteora.backend.dto.response.ProductResponse;
import com.meteora.backend.model.Product;
import com.meteora.backend.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

/**
 * Catalogo publico: cobre a listagem geral, a busca (campo "Digite o produto" do navbar),
 * a listagem por categoria (links "Camisetas", "Bolsas" etc.) e a pagina de detalhe do produto
 * (links "Ver mais" de cada card, que hoje apontam para "#" no front-end estatico).
 */
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public Page<ProductResponse> listar(
            @RequestParam(required = false) String categoria,
            @RequestParam(required = false) String busca,
            Pageable pageable) {

        Page<Product> pagina;
        if (busca != null && !busca.isBlank()) {
            pagina = productService.buscar(busca, pageable);
        } else if (categoria != null && !categoria.isBlank()) {
            pagina = productService.listarPorCategoria(categoria, pageable);
        } else {
            pagina = productService.listar(pageable);
        }
        return pagina.map(ProductResponse::from);
    }

    @GetMapping("/{id}")
    public ProductResponse detalhar(@PathVariable Long id) {
        return ProductResponse.from(productService.buscarPorId(id));
    }
}
