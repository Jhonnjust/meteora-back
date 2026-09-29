package com.meteora.backend.service;

import com.meteora.backend.exception.ResourceNotFoundException;
import com.meteora.backend.model.Order;
import com.meteora.backend.model.OrderStatus;
import com.meteora.backend.model.User;
import com.meteora.backend.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;

    public Page<Order> listarDoUsuario(User usuario, Pageable pageable) {
        return orderRepository.findByUsuario(usuario, pageable);
    }

    public Order buscarDoUsuario(User usuario, Long pedidoId) {
        Order pedido = buscarPorId(pedidoId);
        if (!pedido.getUsuario().getId().equals(usuario.getId())) {
            throw new ResourceNotFoundException("Pedido nao encontrado: " + pedidoId);
        }
        return pedido;
    }

    public Order buscarPorId(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido nao encontrado: " + id));
    }

    // ---- operacoes administrativas ----

    public Page<Order> listarTodos(Pageable pageable) {
        return orderRepository.findAll(pageable);
    }

    @Transactional
    public Order atualizarStatus(Long id, OrderStatus novoStatus) {
        Order pedido = buscarPorId(id);
        pedido.setStatus(novoStatus);
        return orderRepository.save(pedido);
    }
}
