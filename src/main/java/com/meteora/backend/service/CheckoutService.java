package com.meteora.backend.service;

import com.meteora.backend.dto.request.CheckoutRequest;
import com.meteora.backend.exception.BusinessException;
import com.meteora.backend.model.*;
import com.meteora.backend.repository.CartItemRepository;
import com.meteora.backend.repository.OrderRepository;
import com.meteora.backend.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutService {

    private static final BigDecimal DESCONTO_PIX = new BigDecimal("0.05"); // 5% OFF no Pix, conforme a home
    private static final BigDecimal FRETE_PADRAO = new BigDecimal("19.90");
    private static final BigDecimal FRETE_GRATIS_ACIMA_DE = new BigDecimal("300.00");

    private final CartItemRepository cartItemRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;
    private final PaymentService paymentService;

    @Transactional
    public Order finalizarCompra(User usuario, CheckoutRequest req) {
        List<CartItem> itensCarrinho = cartItemRepository.findByUsuario(usuario);

        if (itensCarrinho.isEmpty()) {
            throw new BusinessException("Carrinho vazio - adicione produtos antes de finalizar a compra");
        }

        // 1) valida estoque de todos os itens antes de comprometer qualquer coisa
        for (CartItem item : itensCarrinho) {
            Product produto = item.getProduto();
            if (produto.getEstoque() < item.getQuantidade()) {
                throw new BusinessException("Estoque insuficiente para \"" + produto.getNome() + "\". Disponivel: " + produto.getEstoque());
            }
        }

        // 2) calcula valores
        BigDecimal subtotal = itensCarrinho.stream()
                .map(i -> i.getProduto().getPreco().multiply(BigDecimal.valueOf(i.getQuantidade())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal desconto = req.formaPagamento() == PaymentMethod.PIX
                ? subtotal.multiply(DESCONTO_PIX).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        BigDecimal frete = subtotal.compareTo(FRETE_GRATIS_ACIMA_DE) >= 0 ? BigDecimal.ZERO : FRETE_PADRAO;

        BigDecimal total = subtotal.subtract(desconto).add(frete);

        // 3) monta o pedido
        Address endereco = Address.builder()
                .cep(req.endereco().cep())
                .logradouro(req.endereco().logradouro())
                .numero(req.endereco().numero())
                .complemento(req.endereco().complemento())
                .bairro(req.endereco().bairro())
                .cidade(req.endereco().cidade())
                .estado(req.endereco().estado())
                .build();

        Order pedido = Order.builder()
                .usuario(usuario)
                .enderecoEntrega(endereco)
                .formaPagamento(req.formaPagamento())
                .subtotal(subtotal)
                .desconto(desconto)
                .frete(frete)
                .total(total)
                .status(OrderStatus.AGUARDANDO_PAGAMENTO)
                .build();

        for (CartItem item : itensCarrinho) {
            OrderItem orderItem = OrderItem.builder()
                    .pedido(pedido)
                    .produto(item.getProduto())
                    .nomeProduto(item.getProduto().getNome())
                    .precoUnitario(item.getProduto().getPreco())
                    .quantidade(item.getQuantidade())
                    .build();
            pedido.getItens().add(orderItem);
        }

        // 4) baixa o estoque (reserva definitiva, ja que o pagamento simulado e imediato)
        for (CartItem item : itensCarrinho) {
            item.getProduto().baixarEstoque(item.getQuantidade());
            productRepository.save(item.getProduto());
        }

        // 5) processa o pagamento (mock do gateway)
        PaymentService.ResultadoPagamento resultado = paymentService.processar(pedido);
        pedido.setStatus(resultado.statusResultante());
        pedido.setDetalhesPagamento(resultado.detalhes());

        pedido = orderRepository.save(pedido);

        // 6) esvazia o carrinho apos concluir a compra
        cartItemRepository.deleteByUsuario(usuario);

        return pedido;
    }
}
