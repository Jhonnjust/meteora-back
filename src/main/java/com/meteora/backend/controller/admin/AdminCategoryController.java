package com.meteora.backend.controller.admin;

import com.meteora.backend.dto.request.CategoryRequest;
import com.meteora.backend.dto.response.CategoryResponse;
import com.meteora.backend.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class AdminCategoryController {

    private final CategoryService categoryService;

    @PostMapping
    public ResponseEntity<CategoryResponse> criar(@Valid @RequestBody CategoryRequest req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(CategoryResponse.from(categoryService.criar(req)));
    }

    @PutMapping("/{id}")
    public CategoryResponse atualizar(@PathVariable Long id, @Valid @RequestBody CategoryRequest req) {
        return CategoryResponse.from(categoryService.atualizar(id, req));
    }
}
