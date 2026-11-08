package com.inventario.service.impl;


import com.inventario.dto.CategoryRequestDTO;
import com.inventario.dto.CategoryResponseDTO;
import com.inventario.entity.Category;
import com.inventario.exception.ResourceNotFoundException;
import com.inventario.repository.CategoryRepository;
import com.inventario.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    // Inyección inmutable por constructor gracias a @RequiredArgsConstructor
    private final CategoryRepository categoryRepository;


    @Override
    @Transactional(readOnly = true)
    public List<CategoryResponseDTO> getAllCategories() {
        return categoryRepository.findByActiveTrue()
                .stream()
                .map(this::mapToResponseDTO)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryResponseDTO getCategoryById(Long id) {
        Category category = findActiveCategoryById(id);
        return mapToResponseDTO(category);
    }

    @Override
    @Transactional
    public CategoryResponseDTO createCategory(CategoryRequestDTO requestDTO) {
        Category category = Category.builder()
                .name(requestDTO.getName().trim())
                .description(requestDTO.getDescription())
                .active(true)
                .build();
        Category savedCategory = categoryRepository.save(category);
        return mapToResponseDTO(savedCategory);
    }

    @Override
    @Transactional
    public CategoryResponseDTO updateCategory(Long id, CategoryRequestDTO requestDTO) {
        Category category = findActiveCategoryById(id);
        category.setName(requestDTO.getName().trim());
        category.setDescription(requestDTO.getDescription());
        Category updatedCategory = categoryRepository.save(category);
        return mapToResponseDTO(updatedCategory);
    }

    @Override
    @Transactional
    public void deleteCategory(Long id) {
        Category category = findActiveCategoryById(id);
        // Borrado lógico: cambiamos el estado a inactivo
        category.setActive(false);
        categoryRepository.save(category);
    }

    //Metodo auxiliar privado para reutilizar la búsqueda y validación de 404
    private Category findActiveCategoryById(Long id) {
        return categoryRepository.findById(id)
                .filter(Category::getActive)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
    }

    // Mapper privado: convierte Entity a Response DTO
    private CategoryResponseDTO mapToResponseDTO(Category category) {
        return CategoryResponseDTO.builder()
                .id(category.getId())
                .name(category.getName())
                .description(category.getDescription())
                .build();
    }

}
