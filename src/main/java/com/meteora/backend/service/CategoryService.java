package com.meteora.backend.service;

import com.meteora.backend.dto.request.CategoryRequest;
import com.meteora.backend.exception.BusinessException;
import com.meteora.backend.exception.ResourceNotFoundException;
import com.meteora.backend.model.Category;
import com.meteora.backend.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> listar() {
        return categoryRepository.findAll();
    }

    public Category buscarPorSlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada: " + slug));
    }

    public Category criar(CategoryRequest req) {
        if (categoryRepository.existsBySlug(req.slug())) {
            throw new BusinessException("Ja existe uma categoria com o slug: " + req.slug());
        }
        Category categoria = Category.builder()
                .nome(req.nome())
                .slug(req.slug())
                .imagemUrl(req.imagemUrl())
                .build();
        return categoryRepository.save(categoria);
    }

    public Category atualizar(Long id, CategoryRequest req) {
        Category categoria = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria nao encontrada: " + id));
        categoria.setNome(req.nome());
        categoria.setSlug(req.slug());
        categoria.setImagemUrl(req.imagemUrl());
        return categoryRepository.save(categoria);
    }
}
