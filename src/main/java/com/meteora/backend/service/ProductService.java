package com.meteora.backend.service;

import com.meteora.backend.dto.request.ProductRequest;
import com.meteora.backend.dto.request.StockAdjustRequest;
import com.meteora.backend.exception.ResourceNotFoundException;
import com.meteora.backend.model.Category;
import com.meteora.backend.model.Product;
import com.meteora.backend.repository.CategoryRepository;
import com.meteora.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;

    public Page<Product> listar(Pageable pageable) {
        return productRepository.findByAtivoTrue(pageable);
    }

    public Page<Product> listarPorCategoria(String categoriaSlug, Pageable pageable) {
        return productRepository.findByAtivoTrueAndCategoria_Slug(categoriaSlug, pageable);
    }

    public Page<Product> buscar(String termo, Pageable pageable) {
        return productRepository.buscar(termo, pageable);
    }

    public Product buscarPorId(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + id));
    }

    // ---- operacoes administrativas ----

    @Transactional
    public Product criar(ProductRequest req) {
        Category categoria = categoryRepository.findById(req.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada: " + req.categoriaId()));

        Product produto = Product.builder()
                .nome(req.nome())
                .descricao(req.descricao())
                .preco(req.preco())
                .imagemUrl(req.imagemUrl())
                .estoque(req.estoque())
                .ativo(req.ativo() == null || req.ativo())
                .categoria(categoria)
                .build();

        return productRepository.save(produto);
    }

    @Transactional
    public Product atualizar(Long id, ProductRequest req) {
        Product produto = buscarPorId(id);
        Category categoria = categoryRepository.findById(req.categoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada: " + req.categoriaId()));

        produto.setNome(req.nome());
        produto.setDescricao(req.descricao());
        produto.setPreco(req.preco());
        produto.setImagemUrl(req.imagemUrl());
        produto.setEstoque(req.estoque());
        produto.setCategoria(categoria);
        if (req.ativo() != null) {
            produto.setAtivo(req.ativo());
        }

        return productRepository.save(produto);
    }

    @Transactional
    public Product ajustarEstoque(Long id, StockAdjustRequest req) {
        Product produto = buscarPorId(id);
        int novoEstoque = produto.getEstoque() + req.quantidade();
        if (novoEstoque < 0) {
            throw new IllegalStateException("Ajuste deixaria o estoque negativo");
        }
        produto.setEstoque(novoEstoque);
        return productRepository.save(produto);
    }

    @Transactional
    public void remover(Long id) {
        Product produto = buscarPorId(id);
        produto.setAtivo(false); // soft delete - preserva historico de pedidos ja feitos
        productRepository.save(produto);
    }
}
