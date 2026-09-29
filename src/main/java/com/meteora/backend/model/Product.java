package com.meteora.backend.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false)
    private String nome;

    @Column(length = 1000)
    private String descricao;

    @NotNull
    @DecimalMin(value = "0.0", inclusive = false)
    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal preco;

    private String imagemUrl;

    /** Quantidade disponivel em estoque - controla venda/indisponibilidade */
    @NotNull
    @Min(0)
    @Column(nullable = false)
    @Builder.Default
    private Integer estoque = 0;

    @Builder.Default
    @Column(nullable = false)
    private boolean ativo = true;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category categoria;

    @Builder.Default
    private LocalDateTime criadoEm = LocalDateTime.now();

    /** Reserva estoque; lanca excecao de negocio se nao houver quantidade suficiente */
    public void baixarEstoque(int quantidade) {
        if (this.estoque < quantidade) {
            throw new IllegalStateException("Estoque insuficiente para o produto: " + nome);
        }
        this.estoque -= quantidade;
    }

    public void repor(int quantidade) {
        this.estoque += quantidade;
    }
}
