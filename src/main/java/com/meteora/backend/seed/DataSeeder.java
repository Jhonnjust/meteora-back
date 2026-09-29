package com.meteora.backend.seed;

import com.meteora.backend.model.*;
import com.meteora.backend.repository.CategoryRepository;
import com.meteora.backend.repository.ProductRepository;
import com.meteora.backend.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

/**
 * Popula o banco na primeira execucao com as mesmas categorias e produtos que hoje estao
 * "hardcoded" no index.html do site, alem de um usuario administrador padrao.
 * So roda se as tabelas ainda estiverem vazias, entao e seguro em qualquer ambiente.
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        seedUsuarioAdmin();
        if (categoryRepository.count() == 0) {
            seedCategoriasEProdutos();
        }
    }

    private void seedUsuarioAdmin() {
        if (!userRepository.existsByEmail("admin@meteora.com.br")) {
            User admin = User.builder()
                    .nome("Administrador Meteora")
                    .email("admin@meteora.com.br")
                    .senhaHash(passwordEncoder.encode("admin123"))
                    .role(Role.ROLE_ADMIN)
                    .build();
            userRepository.save(admin);
        }
    }

    private void seedCategoriasEProdutos() {
        Map<String, Category> categorias = new HashMap<>();

        String[][] dadosCategorias = {
                {"Camisetas", "camisetas"},
                {"Bolsas", "bolsas"},
                {"Calçados", "calcados"},
                {"Calças", "calcas"},
                {"Casacos", "casacos"},
                {"Óculos", "oculos"}
        };

        for (String[] dado : dadosCategorias) {
            Category categoria = categoryRepository.save(
                    Category.builder().nome(dado[0]).slug(dado[1]).build()
            );
            categorias.put(dado[1], categoria);
        }

        productRepository.save(Product.builder()
                .nome("Camiseta conforto")
                .descricao("Multicores e tamanhos. Tecido de algodao 100% fresquinho para o verao. Modelagem unissex.")
                .preco(new BigDecimal("70.00"))
                .imagemUrl("/assets/Desktop/produtos/camiseta.png")
                .estoque(50)
                .categoria(categorias.get("camisetas"))
                .build());

        productRepository.save(Product.builder()
                .nome("Calça Alfaiataria")
                .descricao("Modelo Wide Leg alfaiataria em linho. Uma peça para vida toda!")
                .preco(new BigDecimal("180.00"))
                .imagemUrl("/assets/Desktop/produtos/calca.png")
                .estoque(30)
                .categoria(categorias.get("calcas"))
                .build());

        productRepository.save(Product.builder()
                .nome("Tênis Chunky")
                .descricao("Solado alto e confortavel, unissex, ideal para o dia a dia.")
                .preco(new BigDecimal("250.00"))
                .imagemUrl("/assets/Desktop/produtos/tenis.png")
                .estoque(25)
                .categoria(categorias.get("calcados"))
                .build());

        productRepository.save(Product.builder()
                .nome("Jaqueta Jeans")
                .descricao("Jaqueta jeans classica, atemporal e super versatil.")
                .preco(new BigDecimal("150.00"))
                .imagemUrl("/assets/Desktop/produtos/jaqueta.png")
                .estoque(20)
                .categoria(categorias.get("casacos"))
                .build());

        productRepository.save(Product.builder()
                .nome("Óculos Redondo")
                .descricao("Armacao dourada com lentes redondas, protecao UV.")
                .preco(new BigDecimal("120.00"))
                .imagemUrl("/assets/Desktop/produtos/oculos.png")
                .estoque(40)
                .categoria(categorias.get("oculos"))
                .build());

        productRepository.save(Product.builder()
                .nome("Bolsa coringa")
                .descricao("Bolsa de couro versatil, combina com qualquer produção.")
                .preco(new BigDecimal("120.00"))
                .imagemUrl("/assets/Desktop/produtos/bolsa.png")
                .estoque(35)
                .categoria(categorias.get("bolsas"))
                .build());
    }
}
