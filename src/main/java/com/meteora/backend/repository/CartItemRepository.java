package com.meteora.backend.repository;

import com.meteora.backend.model.CartItem;
import com.meteora.backend.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findByUsuario(User usuario);
    Optional<CartItem> findByUsuarioAndProduto_Id(User usuario, Long produtoId);
    void deleteByUsuario(User usuario);
}
