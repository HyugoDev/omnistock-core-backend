package com.inventario.controller;


import com.inventario.dto.CategoryRequestDTO;
import com.inventario.dto.CategoryResponseDTO;
import com.inventario.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    // GET /api/v1/categories -> Lista todas las categorías activas
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }
    // GET /api/v1/categories/{id} -> Busca una categoría por ID
    @GetMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> getCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getCategoryById(id));
    }
    // POST /api/v1/categories -> Crea una nueva categoría (HTTP 201 Created)
    @PostMapping
    public ResponseEntity<CategoryResponseDTO> createCategory(@Valid @RequestBody CategoryRequestDTO requestDTO) {
        CategoryResponseDTO createdCategory = categoryService.createCategory(requestDTO);
        return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
    }
    // PUT /api/v1/categories/{id} -> Actualiza una categoría existente
    @PutMapping("/{id}")
    public ResponseEntity<CategoryResponseDTO> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequestDTO requestDTO) {
        return ResponseEntity.ok(categoryService.updateCategory(id, requestDTO));
    }
    // DELETE /api/v1/categories/{id} -> Desactiva la categoría (HTTP 204 No Content)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
