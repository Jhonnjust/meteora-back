package com.meteora.backend.repository;

import com.meteora.backend.model.Order;
import com.meteora.backend.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByUsuario(User usuario, Pageable pageable);
}
