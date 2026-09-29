package com.meteora.backend.repository;

import com.meteora.backend.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Page<Product> findByAtivoTrue(Pageable pageable);

    Page<Product> findByAtivoTrueAndCategoria_Slug(String slug, Pageable pageable);

    @Query("select p from Product p where p.ativo = true and " +
           "(lower(p.nome) like lower(concat('%', :termo, '%')) " +
           "or lower(p.descricao) like lower(concat('%', :termo, '%')))")
    Page<Product> buscar(@Param("termo") String termo, Pageable pageable);
}
