package com.meteora.backend.controller;

import com.meteora.backend.dto.response.CategoryResponse;
import com.meteora.backend.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryResponse> listar() {
        return categoryService.listar().stream().map(CategoryResponse::from).toList();
    }
}
