package com.meteora.backend.service;

import com.meteora.backend.dto.request.CartItemRequest;
import com.meteora.backend.dto.response.CartItemResponse;
import com.meteora.backend.dto.response.CartResponse;
import com.meteora.backend.exception.BusinessException;
import com.meteora.backend.exception.ResourceNotFoundException;
import com.meteora.backend.model.CartItem;
import com.meteora.backend.model.Product;
import com.meteora.backend.model.User;
import com.meteora.backend.repository.CartItemRepository;
import com.meteora.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;

    public CartResponse obterCarrinho(User usuario) {
        List<CartItem> itens = cartItemRepository.findByUsuario(usuario);
        return montarResposta(itens);
    }

    @Transactional
    public CartResponse adicionarItem(User usuario, CartItemRequest req) {
        Product produto = productRepository.findById(req.produtoId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto nao encontrado: " + req.produtoId()));

        if (!produto.isAtivo()) {
            throw new BusinessException("Produto indisponivel");
        }

        CartItem item = cartItemRepository.findByUsuarioAndProduto_Id(usuario, produto.getId())
                .orElse(CartItem.builder().usuario(usuario).produto(produto).quantidade(0).build());

        int novaQuantidade = item.getQuantidade() + req.quantidade();
        validarEstoque(produto, novaQuantidade);

        item.setQuantidade(novaQuantidade);
        cartItemRepository.save(item);

        return obterCarrinho(usuario);
    }

    @Transactional
    public CartResponse atualizarQuantidade(User usuario, Long produtoId, int quantidade) {
        if (quantidade <= 0) {
            return removerItem(usuario, produtoId);
        }

        CartItem item = cartItemRepository.findByUsuarioAndProduto_Id(usuario, produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item nao esta no carrinho"));

        validarEstoque(item.getProduto(), quantidade);
        item.setQuantidade(quantidade);
        cartItemRepository.save(item);

        return obterCarrinho(usuario);
    }

    @Transactional
    public CartResponse removerItem(User usuario, Long produtoId) {
        CartItem item = cartItemRepository.findByUsuarioAndProduto_Id(usuario, produtoId)
                .orElseThrow(() -> new ResourceNotFoundException("Item nao esta no carrinho"));
        cartItemRepository.delete(item);
        return obterCarrinho(usuario);
    }

    @Transactional
    public void esvaziar(User usuario) {
        cartItemRepository.deleteByUsuario(usuario);
    }

    private void validarEstoque(Product produto, int quantidadeDesejada) {
        if (quantidadeDesejada > produto.getEstoque()) {
            throw new BusinessException("Estoque insuficiente para \"" + produto.getNome() + "\". Disponivel: " + produto.getEstoque());
        }
    }

    private CartResponse montarResposta(List<CartItem> itens) {
        List<CartItemResponse> itensResp = itens.stream().map(CartItemResponse::from).toList();
        BigDecimal total = itensResp.stream()
                .map(CartItemResponse::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        int quantidadeItens = itensResp.stream().mapToInt(CartItemResponse::quantidade).sum();
        return new CartResponse(itensResp, total, quantidadeItens);
    }
}
